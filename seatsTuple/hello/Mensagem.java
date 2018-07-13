import net.jini.core.entry.*;

public class Mensagem implements Entry {
	private String content;

	public Mensagem() {}
	public Mensagem(String word) {
		content = word;
	}

	public String getContent() {
		return content;
	}
}