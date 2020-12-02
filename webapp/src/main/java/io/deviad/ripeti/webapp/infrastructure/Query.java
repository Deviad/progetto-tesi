package io.deviad.ripeti.webapp.infrastructure;

import an.awesome.pipelinr.Command;

public interface Query<C extends Command<R>,R> extends Command.Handler<C, R> {
}
