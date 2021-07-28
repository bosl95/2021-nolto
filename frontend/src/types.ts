import CustomError from 'utils/CustomError';

export enum ButtonStyle {
  SOLID = 'SOLID',
  OUTLINE = 'OUTLINE',
}

export enum FeedStatus {
  PROGRESS = 'PROGRESS',
  COMPLETE = 'COMPLETE',
}

export enum FilterType {
  PROGRESS = 'progress',
  COMPLETE = 'complete',
  SOS = 'sos',
}

export enum SearchType {
  CONTENT = '제목/내용',
  TECH = '기술스택',
}

export interface User {
  id: number;
  nickname: string;
  imageUrl: string;
}

export interface Tech {
  id: number;
  text: string;
}

export interface Feed {
  id: number;
  author: User;
  title: string;
  content: string;
  step: FeedStatus;
  sos: boolean;
  thumbnailUrl?: string;
}

export interface FeedToUpload extends Omit<Feed, 'id' | 'author' | 'thumbnailUrl'> {
  techs: Tech[];
  storageUrl?: string;
  deployedUrl?: string;
  thumbnailImage: File;
}

export interface FeedDetail extends Feed {
  techs: Tech[];
  storageUrl?: string;
  deployedUrl?: string;
  likes: number;
  views: number;
  liked: boolean;
}

export interface LoginInfo {
  email: string;
  password: string;
}

export interface UserInfo {
  id: number;
  socialType: 'GOOGLE' | 'GITHUB';
  nickName: string;
  imageUrl: string;
}

export type OAuthType = 'google' | 'github';

export type SnackBarType = 'error' | 'success' | null;

export type AddSnackBar = (type: SnackBarType, text: string) => void;

export type NotificationType = 'alert' | 'confirm';

export type ErrorHandler = (error: CustomError) => void;
