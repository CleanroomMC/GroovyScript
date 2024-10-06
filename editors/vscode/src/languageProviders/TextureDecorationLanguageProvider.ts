import * as vscode from "vscode";
import { DocumentSelector, Disposable, window as vWindow, workspace as vWorkspace, CancellationTokenSource, TextEditor, languages, DecorationOptions, Uri } from "vscode";
import { TextureDecorationInformation, TextureDecorationProvider } from "../features/TextureDecoration";

export function registerTextureDecorationProvider(selector: DocumentSelector, provider: TextureDecorationProvider): Disposable {
    let cancellationSource = new CancellationTokenSource();

    function cancel() {
        cancellationSource.cancel();
        cancellationSource.dispose();
        cancellationSource = new CancellationTokenSource();
    }

    async function doDecorate(editor: TextEditor): Promise<void> {
        const configuration = vscode.workspace.getConfiguration("groovyscript");
        if (configuration.get<boolean>("enableIcons", true)) {
            const result = await provider.provideTextureDecoration(editor.document, cancellationSource.token);
            if (result) {
                decorate(editor, result);
            }
            return;
        }
        removeDecoration(editor)
        return;
    }

    const editorChangedHandler = async (editor: TextEditor | undefined): Promise<void> => {
        if (editor && languages.match(selector, editor.document)) {
            cancel();
            await doDecorate(editor)
        }
    };
    const changedActiveTextEditor = vWindow.onDidChangeActiveTextEditor(editorChangedHandler);

    editorChangedHandler(vWindow.activeTextEditor);

    const changedDocumentText = vWorkspace.onDidChangeTextDocument(async event => {
        if (vWindow.activeTextEditor?.document === event.document && languages.match(selector, event.document)) {
            cancel();
            await doDecorate(vWindow.activeTextEditor)
        }
    })

    return new Disposable(() => {
        changedActiveTextEditor.dispose();
        changedDocumentText.dispose();
    });
}

function removeDecoration(textEditor: TextEditor) {
    textEditor.setDecorations(decorationStyle, [])
}

function decorate(textEditor: TextEditor, decorations: TextureDecorationInformation[]) {
    textEditor.setDecorations(decorationStyle, decorations.map<DecorationOptions>(decoration => ({
        range: decoration.range,
        hoverMessage: decoration.tooltips,
        renderOptions: {
            before: {
                contentIconPath: Uri.parse(decoration.textureUri, true),
            }
        }
    })))
}

const decorationStyle = vWindow.createTextEditorDecorationType({})