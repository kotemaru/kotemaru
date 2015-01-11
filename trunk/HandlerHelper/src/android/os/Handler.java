package android.os;



/**
 * コンパイル用のダミー.
 * 
 * @author kotemaru.org
 */
public class Handler {
	public Handler(Looper looper) {
	}

	public boolean post(Runnable runner) {
		return true;
	}

	public boolean postDelayed(Runnable runner, long delay) {
		return true;
	}
}
