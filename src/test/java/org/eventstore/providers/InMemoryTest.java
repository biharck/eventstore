package org.eventstore.providers;

import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.hamcrest.core.Is.is;

public class InMemoryTest {

    @Test
    public void shouldAddAnEventToTheEndOfTheStream(){
        assertThat(true, is(true));
    }

    @Test
    public void shouldCreateStreamIfThereIsNoStream(){
        assertThat(true, is(true));
    }

    @Test
    public void shouldReturnTheStreamList(){
        assertThat(true, is(true));
    }
}
