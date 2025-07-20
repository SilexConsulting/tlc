/*
 *  Copyright 2010-2013 Paul Fernley.
 *
 *  This file is part of the Three Ledger Core (TLC) software
 *  from Paul Fernley.
 *
 *  TLC is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  TLC is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TLC. If not, see <http://www.gnu.org/licenses/>.
 */
package org.grails.tlc.sys

import org.hibernate.HibernateException

import java.nio.channels.ClosedByInterruptException
import org.apache.log4j.Logger
import org.hibernate.FlushMode
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.orm.hibernate4.SessionHolder
import org.springframework.transaction.support.TransactionSynchronizationManager


class TaskScanner extends Thread {

    private static final Logger log = Logger.getLogger(TaskScanner)
    static SessionFactory sessionFactory

    TaskExecutor executor
    int delay
    int interval
    int snooze
    long millis
    def bound

    public TaskScanner() {}

    public TaskScanner(TaskExecutor executor, int delay, int interval, int snooze) {
        this.executor = executor
        this.delay = delay
        this.interval = interval
        this.snooze = snooze
        millis = snooze * 1000L
    }

    public void run() {

        try {
            sleep(delay * 1000L)
            while (true) {
                if (Thread.interrupted()) break

                    try {
                        bindSession()
                        executor.scan()
                        unbindSession()
                    } catch (InterruptedException ie) {
                        return
                    } catch (ClosedByInterruptException cbie) {
                        return
                    } catch (Exception ex) {}

                if (Thread.interrupted()) break

                    for (int i = 0; i < interval; i += snooze) {
                        sleep(millis)
                        if (executor.hint.getAndSet(false)) break
                    }
            }
        } catch (InterruptedException ex) {
        } finally {
            unbindSession()
        }

        executor = null     // Be nice to the garbage collector
    }

    // --------------------------------------------- Support Methods ---------------------------------------------

    // Used by Spring to inject the Hibernate session factory
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory
    }

    private bindSession() {
        SessionHolder holder = (SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory)

        if (holder == null) {
            Session session = sessionFactory.openSession()
            holder = new SessionHolder(session)
            TransactionSynchronizationManager.bindResource(sessionFactory, holder)
            bound = true
        } else {
            holder.getSession().clear()
            bound = false
        }
    }

    private unbindSession() {
        if (bound) {
            bound = false
            SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory)
            Session session = sessionHolder.getSession()
            if (session.getFlushMode() != FlushMode.MANUAL) {
                session.flush()
            }
            try {
                session.close()
            } catch (HibernateException e) {
                log.warn("Unable to close Hibernate Session", e)
            }
        }
    }

}
