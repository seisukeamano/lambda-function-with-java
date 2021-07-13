package example.sample01;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * �󂯎�������O���𗘗p���Ĉ��A����Ԃ�Lambda�֐�
 * 
 * @param <UserName> Lambda�֐��̎��s���ɃC���v�b�g�i���N�G�X�g�j�Ƃ��Ď󂯎�閼�O
�@* @param <String>  �A�E�g�v�b�g�i���X�|���X�j�Ƃ��ĕԓ����鈥�A��
 */
public class HelloLambda implements RequestHandler<UserName, String> {

	/**
	 * handleRequest���\�b�h
	 * Lambda�֐��̎��s���N�G�X�g����舵��
	 * @param username ���O
	 * @param context AWS Lambda Context �I�u�W�F�N�g
	 * @return ���A��
	 */
	@Override
	public String handleRequest(UserName username, Context context) {

		String greeting = String.format("Hello %s %s !", username.firstName, username.lastName);
		return greeting;
	}

}

/**
 * ���O�̏����i�[����N���X
 */
class UserName {

	//��
	public String firstName;
	
	//��
	public String lastName;
}
