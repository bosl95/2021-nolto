package com.wooteco.nolto.feed.application;

import com.wooteco.nolto.NotFoundException;
import com.wooteco.nolto.feed.domain.Feed;
import com.wooteco.nolto.feed.domain.Step;
import com.wooteco.nolto.feed.ui.dto.FeedCardResponse;
import com.wooteco.nolto.feed.ui.dto.FeedRequest;
import com.wooteco.nolto.feed.ui.dto.FeedResponse;
import com.wooteco.nolto.image.application.ImageService;
import com.wooteco.nolto.tech.domain.Tech;
import com.wooteco.nolto.tech.domain.TechRepository;
import com.wooteco.nolto.tech.ui.dto.TechResponse;
import com.wooteco.nolto.user.domain.User;
import com.wooteco.nolto.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class FeedServiceTest {
    private FeedRequest FEED_REQUEST1 = new FeedRequest("title1", new ArrayList<>(), "content", "PROGRESS", true,
            "storageUrl", "deployUrl", null);
    private FeedRequest FEED_REQUEST2 = new FeedRequest("title2", new ArrayList<>(), "content", "PROGRESS", true,
            "storageUrl", "deployUrl", null);
    private FeedRequest FEED_REQUEST3 = new FeedRequest("title3", new ArrayList<>(), "content", "PROGRESS", true,
            "storageUrl", "deployUrl", null);

    private User user1 = new User(null, "email@email.com", "password", "user1", "mickey.jpg");
    private User user2 = new User(null, "email2@email.com", "password", "user2", "mickey.jpg");

    private Tech tech1 = new Tech("Spring");
    private Tech tech2 = new Tech("Java");
    private Tech tech3 = new Tech("React");

    @Autowired
    private FeedService feedService;

    @Autowired
    private LikeService likeService;

    @MockBean
    private ImageService imageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TechRepository techRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        given(imageService.upload(any(MultipartFile.class))).willReturn("image.jpg");
        userRepository.save(user1);
        userRepository.save(user2);

        Tech savedTech1 = techRepository.save(tech1);
        Tech savedTech2 = techRepository.save(tech2);
        Tech savedTech3 = techRepository.save(tech3);

        FEED_REQUEST2.setTech(Arrays.asList(savedTech1.getId(), savedTech2.getId()));
        FEED_REQUEST3.setTech(Collections.singletonList(savedTech3.getId()));
    }

    @DisplayName("유저가 feed를 작성한다.")
    @Test
    void create() {
        // when
        Long feedId1 = feedService.create(user1, FEED_REQUEST1);
        Long feedId2 = feedService.create(user1, FEED_REQUEST2);
        Long feedId3 = feedService.create(user2, FEED_REQUEST3);

        em.flush();
        em.clear();

        Feed savedFeed1 = feedService.findEntityById(feedId1);
        Feed savedFeed2 = feedService.findEntityById(feedId2);
        Feed savedFeed3 = feedService.findEntityById(feedId3);

        // then
        assertThat(feedId1).isNotNull();
        assertThat(feedId2).isNotNull();
        assertThat(feedId3).isNotNull();
        피드_정보가_같은지_조회(FEED_REQUEST1, savedFeed1);
        피드_정보가_같은지_조회(FEED_REQUEST2, savedFeed2);
        피드_정보가_같은지_조회(FEED_REQUEST3, savedFeed3);
    }

    @DisplayName("Feed를 Id로 단일 조회하고 조회수를 1 증가시킨다.")
    @Test
    void findById() {
        // given
        Long feedId1 = feedService.create(user1, FEED_REQUEST1);
        Long feedId2 = feedService.create(user2, FEED_REQUEST2);
        Long feedId3 = feedService.create(user2, FEED_REQUEST3);

        em.flush();
        em.clear();

        // when
        FeedResponse feedResponse1 = feedService.findById(user1, feedId1);
        FeedResponse feedResponse2 = feedService.findById(user2, feedId2);
        FeedResponse feedResponse3 = feedService.findById(user2, feedId3);
        FEED_REQUEST1.toEntityWithThumbnailUrl(null);

        // then
        assertThat(feedResponse1.getId()).isEqualTo(feedId1);
        피드_정보가_같은지_조회(FEED_REQUEST1, feedResponse1);
        assertThat(feedResponse1.getViews()).isEqualTo(1);

        assertThat(feedResponse2.getId()).isEqualTo(feedId2);
        피드_정보가_같은지_조회(FEED_REQUEST2, feedResponse2);
        assertThat(feedResponse2.getViews()).isEqualTo(1);

        assertThat(feedResponse3.getId()).isEqualTo(feedId3);
        피드_정보가_같은지_조회(FEED_REQUEST3, feedResponse3);
        assertThat(feedResponse3.getViews()).isEqualTo(1);
    }

    @DisplayName("존재하지 않는 Feed Id로 조회하면 예외가 발생한다.")
    @Test
    void findByNonExistsId() {
        // when then
        assertThatThrownBy(() -> feedService.findById(user1, Long.MAX_VALUE))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("피드를 찾을 수 없습니다.");
    }

    @DisplayName("Feed Id로 entity 객체를 가져올 수 있다.")
    @Test
    void findEntityById() {
        // given
        Long feedId = feedService.create(user1, FEED_REQUEST1);

        // when
        Feed feedEntity = feedService.findEntityById(feedId);

        // then
        assertThat(feedEntity.getId()).isEqualTo(feedId);
        피드_정보가_같은지_조회(FEED_REQUEST1, feedEntity);
    }

    @DisplayName("존재하지 않는 Feed Id로 entity 객체를 조회하면 예외가 발생한다.")
    @Test
    void findEntityByNonExistsId() {
        // when
        assertThatThrownBy(() -> feedService.findEntityById(Long.MAX_VALUE))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("피드를 찾을 수 없습니다.");
    }

    @DisplayName("좋아요 개수가 높은 인기 Feed들을 가져온다.")
    @Test
    void findHotFeeds() {
        // given
        Long firstFeedId = feedService.create(user1, FEED_REQUEST1);
        Long secondFeedId = feedService.create(user1, FEED_REQUEST2);
        Long thirdFeedId = feedService.create(user1, FEED_REQUEST3);

        likeService.addLike(user2, secondFeedId);
        likeService.addLike(user2, thirdFeedId);
        likeService.addLike(user1, secondFeedId);
        em.flush();
        em.clear();

        // when
        List<FeedCardResponse> hotFeeds = feedService.findHotFeeds();

        // then
        assertThat(hotFeeds).hasSize(3);
        피드_정보가_같은지_조회(FEED_REQUEST2, hotFeeds.get(0));
        피드_정보가_같은지_조회(FEED_REQUEST3, hotFeeds.get(1));
        피드_정보가_같은지_조회(FEED_REQUEST1, hotFeeds.get(2));
    }

    @DisplayName("모든 피드를 조회한다. (최신 등록된 id 순)")
    @Test
    void findAllWithAllFilter() {
        // given
        String defaultFilter = "all";
        feedService.create(user1, FEED_REQUEST1);
        feedService.create(user1, FEED_REQUEST2);
        feedService.create(user1, FEED_REQUEST3);

        // when
        List<FeedCardResponse> allFeeds = feedService.findAll(defaultFilter);

        // then
        assertThat(allFeeds.size()).isEqualTo(3);
        피드_정보가_같은지_조회(FEED_REQUEST1, allFeeds.get(2));
        피드_정보가_같은지_조회(FEED_REQUEST2, allFeeds.get(1));
        피드_정보가_같은지_조회(FEED_REQUEST3, allFeeds.get(0));
    }

    @DisplayName("SOS 피드를 조회한다. (최신 등록된 id 순)")
    @Test
    void findAllWithFilterSOS() {
        // given
        FEED_REQUEST1.setSos(true);
        FEED_REQUEST2.setSos(false);
        FEED_REQUEST3.setSos(true);

        String defaultFilter = "sos";
        feedService.create(user1, FEED_REQUEST1);
        feedService.create(user1, FEED_REQUEST2);
        feedService.create(user1, FEED_REQUEST3);

        // when
        List<FeedCardResponse> allFeeds = feedService.findAll(defaultFilter);

        // then
        assertThat(allFeeds.size()).isEqualTo(2);
        피드_정보가_같은지_조회(FEED_REQUEST1, allFeeds.get(1));
        피드_정보가_같은지_조회(FEED_REQUEST3, allFeeds.get(0));
    }

    @DisplayName("PROGRESS 피드를 조회한다. (최신 등록된 id 순)")
    @Test
    void findAllWithFilterProgress() {
        // given
        FEED_REQUEST1.setStep(Step.COMPLETE.name());
        FEED_REQUEST2.setStep(Step.PROGRESS.name());
        FEED_REQUEST3.setStep(Step.PROGRESS.name());

        feedService.create(user1, FEED_REQUEST1);
        feedService.create(user1, FEED_REQUEST2);
        feedService.create(user1, FEED_REQUEST3);

        // when
        List<FeedCardResponse> allFeeds = feedService.findAll(Step.PROGRESS.name());

        // then
        assertThat(allFeeds.size()).isEqualTo(2);
        피드_정보가_같은지_조회(FEED_REQUEST3, allFeeds.get(0));
        피드_정보가_같은지_조회(FEED_REQUEST2, allFeeds.get(1));
    }

    @DisplayName("COMPLETE 피드를 조회한다. (최신 등록된 id 순)")
    @Test
    void findAllWithFilterComplete() {
        // given
        FEED_REQUEST1.setStep(Step.COMPLETE.name());
        FEED_REQUEST2.setStep(Step.COMPLETE.name());
        FEED_REQUEST3.setStep(Step.PROGRESS.name());

        feedService.create(user1, FEED_REQUEST1);
        feedService.create(user1, FEED_REQUEST2);
        feedService.create(user1, FEED_REQUEST3);

        // when
        List<FeedCardResponse> allFeeds = feedService.findAll(Step.COMPLETE.name());

        // then
        피드_정보가_같은지_조회(FEED_REQUEST2, allFeeds.get(0));
        피드_정보가_같은지_조회(FEED_REQUEST1, allFeeds.get(1));
    }

    private void 피드_정보가_같은지_조회(FeedRequest request, Feed feed) {
        List<Long> techIds = feed.getTechs().stream()
                .map(Tech::getId)
                .collect(Collectors.toList());

        assertThat(request.getTitle()).isEqualTo(feed.getTitle());
        assertThat(request.getTech()).containsExactlyElementsOf(techIds);
        assertThat(request.getContent()).isEqualTo(feed.getContent());
        assertThat(request.getStep()).isEqualTo(feed.getStep().name());
        assertThat(request.isSos()).isEqualTo(feed.isSos());
        assertThat(request.getStorageUrl()).isEqualTo(feed.getStorageUrl());
        assertThat(request.getDeployedUrl()).isEqualTo(feed.getDeployedUrl());
    }

    private void 피드_정보가_같은지_조회(FeedRequest request, FeedResponse response) {
        List<Long> techIds = response.getTechs().stream()
                .map(TechResponse::getId)
                .collect(Collectors.toList());

        assertThat(request.getTitle()).isEqualTo(response.getTitle());
        assertThat(request.getTech()).containsExactlyElementsOf(techIds);
        assertThat(request.getContent()).isEqualTo(response.getContent());
        assertThat(request.getStep()).isEqualTo(response.getStep());
        assertThat(request.isSos()).isEqualTo(response.isSos());
        assertThat(request.getStorageUrl()).isEqualTo(response.getStorageUrl());
        assertThat(request.getDeployedUrl()).isEqualTo(response.getDeployedUrl());
    }

    private void 피드_정보가_같은지_조회(FeedRequest request, FeedCardResponse response) {
        assertThat(request.getTitle()).isEqualTo(response.getTitle());
        assertThat(request.getContent()).isEqualTo(response.getContent());
        assertThat(request.getStep()).isEqualTo(response.getStep());
        assertThat(request.isSos()).isEqualTo(response.isSos());
    }
}