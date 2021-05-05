# photo-sliding-puzzle

## 프로젝트 구성 & 사용 언어

***

- 안드로이드 스튜디오 (Android Studio)
- JAVA

***

## 프로젝트 소개

- 슬라이딩 퍼즐 게임을 만든 프로젝트입니다.
- 사용자의 갤러리에서 원하는 이미지를 선택하여 게임을 진행할 수 있습니다.
- 해당 프로젝트는 안드로이드 스튜디오에서 작성하였고, Minimum sdk version은 21이고, target sdk version은 30입니다.

***

## 실행화면 보기

- __Pixel 3a API 30__ 기기에서 테스트되었습니다.

### 1. 초기 화면
![image](https://user-images.githubusercontent.com/71871348/117125598-b538c280-add4-11eb-893f-7f3ec599e256.png)
- 어플 상단의 __info__ 버튼을 클릭하면, 게임에 대한 설명과 게임에 사용할 이미지를 선택할 수 있는 방법을 안내합니다.
- [3×3] 혹은 [4×4] 버튼을 클릭해서 게임의 난이도를 선택할 수 있습니다.


### 2. 사용자 갤러리의 이미지 선택하기

![실행화면1](https://user-images.githubusercontent.com/71871348/117126202-66d7f380-add5-11eb-97ac-98ccdc9136a7.gif)

- 상단의 이미지를 클릭하면, 사용자의 갤러리로 이동하여 게임에 사용할 이미지를 선택할 수 있습니다.
- 게임에 사용될 이미지는 정사각형이어야 하기 때문에 image crop 기능을 사용합니다.
  - 해당 어플에서 image crop은 [android-image-cropper](https://github.com/ArthurHub/Android-Image-Cropper) 라이브러리를 사용하였습니다.
- 원하는 이미지를 선택하고, 원하는 사이즈로 crop하면 퍼즐 보드가 선택한 이미지로 변경됩니다.

### 3. 게임 시작하기

![실행화면2](https://user-images.githubusercontent.com/71871348/117126678-11501680-add6-11eb-88fd-87e5bc6e50a9.gif)

- 하단의 [START] 버튼을 클릭하여 게임을 시작할 수 있습니다.
- 게임을 시작하면 왼쪽 하단에서 퍼즐을 몇 번 이동시켰는지를 확인할 수 있습니다.
- 정답을 맞추게 되면 하단의 toast 메시지로 "성공!" 메시지를 확인할 수 있습니다.

***

## 배포

현재 해당 어플은 플레이스토어에 출시되었습니다.

[사진 슬라이딩 퍼즐](https://play.google.com/store/apps/details?id=com.slidingpuzzle.photoslidingpuzzle) 👈 이 링크에서 다운받으실 수 있습니다.

***

## 버전

[ver1.0] 
[ver1.0.1] 보드판 크기 수정, 이동 횟수 위치 변경

***
