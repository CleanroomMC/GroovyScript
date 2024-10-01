import { CancellationToken, Disposable, languages, ProviderResult, TextDocument, Range as VRange } from 'vscode';
import { ClientCapabilities, DocumentColorOptions, DocumentSelector, DynamicFeature, ensure, FeatureClient, MessageDirection, PartialResultParams, ProtocolRequestType, RequestHandler, ServerCapabilities, StaticRegistrationOptions, TextDocumentIdentifier, TextDocumentLanguageFeature, TextDocumentRegistrationOptions, WorkDoneProgressOptions, WorkDoneProgressParams } from 'vscode-languageclient';
import { registerTextureDecorationProvider } from '../languageProviders/TextureDecorationLanguageProvider';

export interface TextureDecorationParams extends WorkDoneProgressParams, PartialResultParams {
    /**
     * The text document.
     */
    textDocument: TextDocumentIdentifier;
}

export interface TextureDecorationInformation {
    range: VRange;
    textureUri: string;
}

export interface TextureDecorationOptions extends WorkDoneProgressOptions {
}

export interface TextureDecorationRegistrationOptions extends TextDocumentRegistrationOptions, StaticRegistrationOptions, DocumentColorOptions {
}

export interface ProvideTextureDecorationsSignature {
    (document: TextDocument, token: CancellationToken): ProviderResult<TextureDecorationInformation[]>;
}

export interface TextureDecorationProvider {
    provideTextureDecoration(document: TextDocument, token: CancellationToken): ProviderResult<TextureDecorationInformation[]>;
}

export interface TextureDecorationMiddleware {
    provideTextureDecorations?: (this: void, document: TextDocument, token: CancellationToken, next: ProvideTextureDecorationsSignature) => ProviderResult<TextureDecorationInformation[]>;
}

export namespace TextureDecorationRequest {
    export const method: 'groovyScript/textureDecoration' = 'groovyScript/textureDecoration';
    export const messageDirection = MessageDirection.clientToServer;
    export const type = new ProtocolRequestType<TextureDecorationParams, TextureDecorationInformation[], TextureDecorationInformation[], void, TextureDecorationRegistrationOptions>(method);
    export type HandlerSignature = RequestHandler<TextureDecorationParams, TextureDecorationInformation[], void>;
}

export class TextureDecorationFeature extends TextDocumentLanguageFeature<boolean | TextureDecorationOptions, TextureDecorationRegistrationOptions, TextureDecorationProvider, TextureDecorationMiddleware> {
    constructor(client: FeatureClient<TextureDecorationMiddleware>) {
        super(client, TextureDecorationRequest.type);
    }
    fillClientCapabilities(capabilities: ClientCapabilities): void {
        ensure(ensure(capabilities, 'experimental')!, 'textureDecorationProvider')!.dynamicRegistration = true;
    }
    initialize(capabilities: ServerCapabilities, documentSelector: DocumentSelector): void {
        const [id, options] = this.getRegistration(documentSelector, capabilities.experimental.textureDecorationProvider);
        if (!id || !options) {
            return;
        }
        this.register({ id: id, registerOptions: options });
    }
    protected registerLanguageProvider(options: TextureDecorationRegistrationOptions, id: string): [Disposable, TextureDecorationProvider] {
        const selector = options.documentSelector!;

        const provider: TextureDecorationProvider = {
            provideTextureDecoration: (document, token) => {
                const client = this._client;
                const provideTextureDecorations: ProvideTextureDecorationsSignature = (document, token) => {
                    const requestParams: TextureDecorationParams = {
                        textDocument: client.code2ProtocolConverter.asTextDocumentIdentifier(document),
                    };

                    return client.sendRequest(TextureDecorationRequest.type, requestParams, token).then((result) => {
                        if (token.isCancellationRequested) {
                            return null;
                        }
                        return result.map<TextureDecorationInformation>(decoration => ({
                            range: decoration.range,
                            textureUri: client.protocol2CodeConverter.asUri(decoration.textureUri).toString(true),
                        }));
                    }, (error) => {
                        return client.handleFailedRequest(TextureDecorationRequest.type, token, error, null);
                    });
                };
                const middleware = client.middleware;
                return middleware.provideTextureDecorations
                    ? middleware.provideTextureDecorations(document, token, provideTextureDecorations)
                    : provideTextureDecorations(document, token);
            },
        };

        return [registerTextureDecorationProvider(this._client.protocol2CodeConverter.asDocumentSelector(selector), provider), provider];
    }

}