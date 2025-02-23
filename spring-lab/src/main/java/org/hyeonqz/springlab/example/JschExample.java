package org.hyeonqz.springlab.example;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JschExample {

/*	public void Example() throws SftpException {
		ChannelSftp channelSftp = new ChannelSftp();

		*//**
		 * @param : 이동할 경로를 적는다
		 * '/home/hkjin' 경로로 이동한다.
		 * *//*
		channelSftp.cd("/home/hkjin");

		// 현재 경로를 체크한다.
		String pwd = channelSftp.pwd();
		log.info("현재 경로는 : {}", pwd);

		*//**
		 * @Return : 보통 파일의 메모리에 담아서 작업을 하기에 반환값은 InputStream 이다.
		 * @Param : 읽어올 파일 이름을 적는다.
		 * 현재 경로에서 'abc.txt' 파일을 가져온다.
		 * *//*
		InputStream inputStream = channelSftp.get("abc.txt");


		*//**
		 * @Param1 : InputStream 에 담긴 byte 파일
		 * @Param2 : 파일을 넣고 싶은 경로
		 * @Param3 : [Optional] 조건이 있으면 넣는다 ex) OVERWRITE(덮어쓰기)
		 * 파일을 원하는 경로에 넣는다 -> 같은 파일이 있으면 덮어쓰기 진행한다. .
		 * *//*
		channelSftp.put(inputStream, path, ChannelSftp.OVERWRITE);


		*//**
		 * @Param : 폴더를 생성할 경로
		 * 원하는 경로에 폴더를 생성한다.
		 * *//*
		channelSftp.mkdir("/home/hkjin/java");


		*//**
		 * @Param : 권한 부여
		 * @Param : 권한 부여할 폴더 경로
		 * 위 폴더에 권한을 준다.
		 * *//*
		channelSftp.chmod(0_660, "/home/hkjin/java");


		*//**
		 * @Return : String
		 * 서버의 '루트' 디렉토리로 돌아간다.
		 * *//*
		String home = channelSftp.getHome();

		*//**
		 * @Param : 받아온 경로에 무슨 파일이 있나 체크한다.
		 * 위 경로에 어떠한 파일이 있나 체크한다.
		 * *//*
		channelSftp.ls("/home/hkjin/java");


		*//**
		 * sftp 세션을 종료한다.
		 * *//*
		channelSftp.disconnect();

	}*/
}
