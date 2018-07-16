import net.jini.space.*;
import net.jini.core.lease.*;
//import JavaSpacesUtils.SpaceAccessor;

public class hello {
	
	public static void main(String[] args) {
		
		// operations:
		// -> write
		// -> read
		// -> take
		try {
			Mensagem msg = new Mensagem("Hello World!");
			JavaSpace space = SpaceUtils.getSpace("localhost");
			space.write(msg, null, Lease.FOREVER);

			Mensagem template = new Mensagem();
			Mensagem result = (Mensagem) space.read(template, null, Long.MAX_VALUE);

			System.out.println(result.getContent());
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
}