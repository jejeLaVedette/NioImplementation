package multiCast.client.kernel.nioImplementation;

/**
 * 
 * @author Jérôme
 *
 */
public enum State {
	READING_LENGTH,READING_MSG,WRITING_LENGTH,WRITING_MSG
}
