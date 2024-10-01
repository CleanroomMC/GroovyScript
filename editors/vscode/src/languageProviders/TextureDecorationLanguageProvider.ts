import { DocumentSelector, Disposable, window as vWindow, workspace as vWorkspace, CancellationTokenSource, TextEditor, TextDocument, languages, DecorationOptions, Uri } from "vscode";
import { TextureDecorationInformation, TextureDecorationProvider } from "../features/TextureDecoration";

export function registerTextureDecorationProvider(selector: DocumentSelector, provider: TextureDecorationProvider): Disposable {
    let cancellationSource = new CancellationTokenSource();

    function cancel() {
        cancellationSource.cancel();
        cancellationSource.dispose();
        cancellationSource = new CancellationTokenSource();
    }

    const editorChangedHandler = async (editor: TextEditor | undefined): Promise<void> => {
        if (editor && languages.match(selector, editor.document)) {
            cancel();
            const result = await provider.provideTextureDecoration(editor.document, cancellationSource.token);

            if (result) {
                decorate(editor, result);
            }
        }
    };
    const changedActiveTextEditor = vWindow.onDidChangeActiveTextEditor(editorChangedHandler);

    editorChangedHandler(vWindow.activeTextEditor);

    const changedDocumentText = vWorkspace.onDidChangeTextDocument(async event => {
        if (vWindow.activeTextEditor?.document === event.document && languages.match(selector, event.document)) {
            cancel();
            const result = await provider.provideTextureDecoration(event.document, cancellationSource.token);
            if (result) {
                decorate(vWindow.activeTextEditor, result);
            }
        }
    })

    return new Disposable(() => {
        changedActiveTextEditor.dispose();
        changedDocumentText.dispose();
    });
}

function decorate(textEditor: TextEditor, decorations: TextureDecorationInformation[]) {
    textEditor.setDecorations(decorationStyle, decorations.map<DecorationOptions>(decoration => ({
        range: decoration.range,
        renderOptions: {
            before: {
                contentIconPath: Uri.parse(decoration.textureUri, true),
            }
        }
    })))
}

const decorationStyle = vWindow.createTextEditorDecorationType({})