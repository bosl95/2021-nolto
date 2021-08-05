package com.wooteco.nolto.feed.ui;

import com.wooteco.nolto.auth.MemberAuthenticationPrincipal;
import com.wooteco.nolto.auth.UserAuthenticationPrincipal;
import com.wooteco.nolto.feed.application.CommentService;
import com.wooteco.nolto.feed.ui.dto.ReplyRequest;
import com.wooteco.nolto.feed.ui.dto.ReplyResponse;
import com.wooteco.nolto.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/feeds/{feedId:[\\d]+}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{commentId}/replies")
    public ResponseEntity<ReplyResponse> createReply(@MemberAuthenticationPrincipal User user,
                                                     @PathVariable Long feedId,
                                                     @PathVariable Long commentId,
                                                     @RequestBody ReplyRequest request) {
        ReplyResponse replyResponse = commentService.createReply(user, feedId, commentId, request);
        return ResponseEntity
                .created(URI.create("/feeds/" + feedId + "/comments/" + commentId + "/replies/" + replyResponse.getId()))
                .body(replyResponse);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<List<ReplyResponse>> findAllRepliesById(@UserAuthenticationPrincipal User user,
                                                        @PathVariable Long feedId,
                                                        @PathVariable Long commentId) {
        List<ReplyResponse> replyResponses = commentService.findAllRepliesById(user, feedId, commentId);
        return ResponseEntity.ok(replyResponses);
    }

    @PutMapping("/{commentId}/replies/{replyId}")
    public ResponseEntity<ReplyResponse> updateReply(@UserAuthenticationPrincipal User user,
                                                @PathVariable Long feedId,
                                                @PathVariable Long commentId,
                                                @PathVariable Long replyId,
                                                @RequestBody ReplyRequest request) {
        ReplyResponse updateReplyResponse = commentService.updateReply(user, feedId, commentId, replyId, request);
        return ResponseEntity.ok(updateReplyResponse);
    }

    @DeleteMapping("/{commentId}/replies/{replyId}")
    public ResponseEntity<Void> deleteReply(@UserAuthenticationPrincipal User user,
                                            @PathVariable Long feedId,
                                            @PathVariable Long commentId,
                                            @PathVariable Long replyId) {
        commentService.deleteReply(user, feedId, commentId, replyId);
        return ResponseEntity.noContent().build();
    }
}
