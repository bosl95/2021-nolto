import styled, { css } from 'styled-components';

import TextButton from 'components/@common/TextButton/TextButton';
import TextInput from 'components/@common/TextInput/TextInput';
import { PALETTE } from 'constants/palette';
import { ButtonStyle } from 'types';

const Author = styled.div`
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 14px;
  font-weight: 600;
  margin: 0.5rem 0;

  & > span {
    color: ${PALETTE.PRIMARY_400};
    font-weight: 400;
  }
`;

const modify = css`
  width: 100%;

  & > span,
  input {
    width: 100%;
  }
`;

const Body = styled.span<{ isModifying: boolean }>`
  display: inline-block;
  padding: 0 1.5rem;
  ${({ isModifying }) => isModifying && modify};
`;

const Content = styled.span<{ isFeedAuthor: boolean }>`
  display: inline-block;
  padding: 0 1.5rem;
  box-shadow: 2px 2px 4px 2px rgba(85, 85, 85, 0.1);
  border-radius: 0.75rem;
  min-width: 12rem;
  height: 2.5rem;
  line-height: 2.5rem;

  background-color: ${({ isFeedAuthor }) => isFeedAuthor && PALETTE.ORANGE_200};
`;

export const ModifyTextInput = styled(TextInput)`
  font-size: 1rem;
  border-color: ${PALETTE.GRAY_300};
  padding: 0;
  padding-bottom: 2px;
  transition: border-color 0.1s ease;

  &:focus {
    border-color: ${PALETTE.GRAY_400};
  }
`;

const Detail = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 0.25rem;
  font-size: 14px;
`;

const ThumbUpWrapper = styled.div`
  display: flex;
  gap: 2px;
`;

const ReplyFromWrapper = styled.div`
  margin-top: 0.5rem;
  padding: 0 1.5rem;
`;

export const CommentTextButton = styled(TextButton.Regular)`
  color: ${PALETTE.BLACK_400};
  padding: 2px;
`;

CommentTextButton.defaultProps = {
  buttonStyle: ButtonStyle.SOLID,
  reverse: true,
};

export default { Author, Content, Detail, Body, ThumbUpWrapper, ReplyFromWrapper };
