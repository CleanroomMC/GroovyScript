import * as net from "net";
import * as lc from "vscode-languageclient/node";
import * as vscode from "vscode";
import { extensionStatusBar } from "./gui/extensionStatusBarProvider";

let client: lc.LanguageClient;
let outputChannel = vscode.window.createOutputChannel("GroovyScript Language Server");
let traceOutputChannel = vscode.window.createOutputChannel("GroovyScript Language Server Trace");

async function startClient() {
	const serverOptions = () => {
		const configuration = vscode.workspace.getConfiguration("groovyscript");
		let port = configuration.get<number>("port", 25564);
		outputChannel.appendLine(`Connecting to GroovyScript Language Server at port ${port}`);
		let socket = net.connect({ port: port });
		socket.on("error", (err) => {
			extensionStatusBar.setError();
			outputChannel.appendLine(err.toString());
			stopClient();
		});
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

	client = new lc.LanguageClient("groovyscript", "GroovyScript", serverOptions, clientOptions)

	try {
		await client.start();
	} catch (e) {
		extensionStatusBar.setError();
		outputChannel.appendLine(e.toString());
		return;
	}

	extensionStatusBar.running();
	outputChannel.appendLine("Connected to GroovyScript Language Server");
}

async function stopClient() {
	if (!client || !client.isRunning()) return;
	try {
		await client.stop();
	} catch {
	}
}

export async function activate(context: vscode.ExtensionContext) {
	let disposable = vscode.commands.registerCommand("groovyscript.reconnect", async () => {
		outputChannel.appendLine("Reconnecting...");
		await stopClient();
		extensionStatusBar.startUp();
		await startClient();
	});

	context.subscriptions.push(disposable);

	context.subscriptions.push(extensionStatusBar);
	extensionStatusBar.startUp();

	await startClient();
}

export function deactivate(): Thenable<void> | undefined {
	return stopClient();
}
