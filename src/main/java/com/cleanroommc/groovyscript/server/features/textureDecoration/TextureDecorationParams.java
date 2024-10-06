package com.cleanroommc.groovyscript.server.features.textureDecoration;

import org.eclipse.lsp4j.PartialResultParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.WorkDoneProgressParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

public class TextureDecorationParams implements WorkDoneProgressParams, PartialResultParams {

    private TextDocumentIdentifier textDocument;

    private Either<String, Integer> partialResultToken;
    private Either<String, Integer> workDoneToken;

    public TextureDecorationParams(TextDocumentIdentifier textDocument, Either<String, Integer> partialResultToken, Either<String, Integer> workDoneToken) {
        this.textDocument = textDocument;
        this.partialResultToken = partialResultToken;
        this.workDoneToken = workDoneToken;
    }

    public TextDocumentIdentifier getTextDocument() {
        return textDocument;
    }

    public void setTextDocument(TextDocumentIdentifier textDocument) {
        this.textDocument = textDocument;
    }

    @Override
    public Either<String, Integer> getPartialResultToken() {
        return partialResultToken;
    }

    @Override
    public void setPartialResultToken(Either<String, Integer> token) {
        this.partialResultToken = token;
    }

    @Override
    public Either<String, Integer> getWorkDoneToken() {
        return workDoneToken;
    }

    @Override
    public void setWorkDoneToken(Either<String, Integer> token) {
        this.workDoneToken = token;
    }

}
