import * as net from "net";
import * as lc from "vscode-languageclient/node";
import * as vscode from "vscode";

let client: lc.LanguageClient;
let outputChannel: vscode.OutputChannel;
let traceOutputChannel: vscode.OutputChannel;

async function startClient() {
	if (!outputChannel) {
		outputChannel = vscode.window.createOutputChannel("GroovyScript Language Server");
	}

	if (!traceOutputChannel) {
		traceOutputChannel = vscode.window.createOutputChannel("GroovyScript Language Server Trace");
	}

	const serverOptions = () => {
		const configuration = vscode.workspace.getConfiguration("groovyscript");
		let socket = net.connect({ port: configuration.get<number>("port") ?? 8000 });
		let result: lc.StreamInfo = {
			writer: socket,
			reader: socket
		};
		return Promise.resolve(result);
	};

	const clientOptions: lc.LanguageClientOptions = {
		documentSelector: [
			{ scheme: "file", language: "groovy" },
			{ scheme: "file", pattern: "*.groovy" },
			{ scheme: "file", pattern: "*.gvy" },
			{ scheme: "file", pattern: "*.gy" },
			{ scheme: "file", pattern: "*.gsh" },
		],
		outputChannel,
		traceOutputChannel,
	};

	client = new lc.LanguageClient("groovyscript", "groovyscript", serverOptions, clientOptions);
	await client.start();
}

async function stopClient() {
	if (!client) return;
	await client.stop();
}

export async function activate(context: vscode.ExtensionContext) {
    // FIXME: this does not work if the server was restarted.
	let disposable = vscode.commands.registerCommand("groovyscript.reconnect", async () => {
		await stopClient();
		await startClient();
	});

	context.subscriptions.push(disposable);

	await startClient();
}

export function deactivate(): Thenable<void> | undefined {
	return stopClient();
}
