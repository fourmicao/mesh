/**
 * Copyright 2004 - 2016 Syncleus, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.syncleus.ferma.tx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Mockito;

import com.syncleus.ferma.FramedTransactionalGraph;

public class TxFactoryTest implements TxFactory {

    private Tx mock = Mockito.mock(Tx.class);

    @Test
    public void testTx0() {
        try (Tx tx = tx()) {

        }
        verify(mock).close();
    }

    @Test
    public void testTx1() {
        tx(() -> {

        });
        verify(mock).close();
    }

    @Test
    public void testTx2() {
        assertEquals("test", tx(() -> {
            return "test";
        }));
        verify(mock).close();
    }

    @Test
    public void testTx3() {
        assertEquals("test", tx((tx) -> {
            tx.failure();
            tx.success();
            return "test";
        }));
        verify(mock).close();
    }

    @Test
    public void testAbstractTxSucceeding() {
        @SuppressWarnings("unchecked")
        AbstractTx<FramedTransactionalGraph> tx = Mockito.mock(AbstractTx.class, Mockito.CALLS_REAL_METHODS);
        FramedTransactionalGraph graph = Mockito.mock(FramedTransactionalGraph.class);
        tx.init(graph);
        try (Tx tx2 = tx) {
            assertNotNull(Tx.getActive());
            tx2.success();
        }
        assertNull(Tx.getActive());
        verify(tx).close();
        verify(graph).commit();
        verify(graph).close();
        verify(graph).shutdown();
        verify(graph, Mockito.never()).rollback();
    }

    @Test
    public void testAbstractTxDefault() {
        @SuppressWarnings("unchecked")
        AbstractTx<FramedTransactionalGraph> tx = Mockito.mock(AbstractTx.class, Mockito.CALLS_REAL_METHODS);
        FramedTransactionalGraph graph = Mockito.mock(FramedTransactionalGraph.class);
        tx.init(graph);
        try (Tx tx2 = tx) {
            assertNotNull(Tx.getActive());
            // Don't call tx2.success() or tx2.failure()
        }
        assertNull(Tx.getActive());
        verify(tx).close();
        verify(graph).rollback();
        verify(graph).close();
        verify(graph).shutdown();
        verify(graph, Mockito.never()).commit();
    }

    @Test
    public void testAbstractTxFailing() {
        @SuppressWarnings("unchecked")
        AbstractTx<FramedTransactionalGraph> tx = Mockito.mock(AbstractTx.class, Mockito.CALLS_REAL_METHODS);
        FramedTransactionalGraph graph = Mockito.mock(FramedTransactionalGraph.class);
        tx.init(graph);
        try (Tx tx2 = tx) {
            assertNotNull(Tx.getActive());
            tx2.failure();
        }
        assertNull(Tx.getActive());
        verify(tx).close();
        verify(graph).rollback();
        verify(graph).close();
        verify(graph).shutdown();
        verify(graph, Mockito.never()).commit();
    }

    @Override
    public Tx tx() {
        return mock;
    }

    @Override
    public <T> T tx(TxAction<T> txHandler) {
        try (Tx tx = tx()) {
            try {
                return txHandler.handle(mock);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
