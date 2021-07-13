package example.sample01;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Hello implements RequestHandler<UserName, String> {

	/**
	 * AWS Lambda �̃n���h���[���\�b�h
	 *
	 * @param username ���[�U�[���i�C���v�b�g�Ƃ��Ċ֐��̎��s���ɓn���ꂽ�l�j
	 * @param context AWS Lambda Context �I�u�W�F�N�g
	 * @return �o�̓f�[�^
	 */
	@Override
	public String handleRequest(UserName username, Context context) {
		
		String greeting = String.format("Hello %s %s !", username.firstName, username.lastName);
		return greeting;
		
	}

}
