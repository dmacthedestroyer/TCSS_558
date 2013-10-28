package tcss558.homework2.test;

import org.junit.Test;

import tcss558.homework2.ConcurrentTCPSpellingServer;

public class ConcurrentTCPSpellingServerValidationTest {
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenArgsEmpty() {
		ConcurrentTCPSpellingServer.newServer(new String[0]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenArgsLength1() {
		ConcurrentTCPSpellingServer.newServer(new String[] { "12500" });
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenPortNumberMalformed() {
		ConcurrentTCPSpellingServer.newServer(new String[] { "1250f", "test" });
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenPortNumberExceedRange() {
		ConcurrentTCPSpellingServer.newServer(new String[] { "65536", "test" });
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenPortNumberLTZero() {
		ConcurrentTCPSpellingServer.newServer(new String[] { "-1", "test" });
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenFileNotFound() {
		// I don't know how the fuck to load resources from ecplise/java, so this
		// test just won't get done.
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenMaxSessionsMalformed() {
		// I can't get past the check for the file location, so I can't test this
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenMaxSessionsLTEZero() {
		// I can't get past the check for the file location, so I can't test this
	}
}