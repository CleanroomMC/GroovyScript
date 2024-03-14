////////////////////////////////////////////////////////////////////////////////
// Copyright 2021 DontShaveTheYak
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License
//
// Author: DontShaveTheYak
// No warranty of merchantability or fitness of any kind.
// Use this software at your own risk.
////////////////////////////////////////////////////////////////////////////////

import { StatusBarItem, window, StatusBarAlignment } from "vscode";
import { Disposable } from "vscode-languageclient";

class ExtensionStatusBarProvider implements Disposable {
	private statusBarItem: StatusBarItem;

	constructor() {
		this.statusBarItem = window.createStatusBarItem(StatusBarAlignment.Right, Number.MIN_VALUE);
	}

	public startUp(): void {
		this.statusBarItem.text = StatusIcon.launching;
		this.statusBarItem.tooltip = 'GroovyScript: Connecting';
		this.statusBarItem.show();
	}

	public running(): void {
		this.statusBarItem.text = StatusIcon.ready;
		this.statusBarItem.tooltip = 'GroovyScript: Ready';
	}

	public updateText(text: string): void {
		this.statusBarItem.text = text;
	}

	public setBusy(): void {
		this.statusBarItem.text = StatusIcon.busy;
	}

	public setError(): void {
		this.statusBarItem.text = StatusIcon.error;
	}

	public setReady(): void {
		this.statusBarItem.text = StatusIcon.ready;
	}

	public updateTooltip(tooltip: string): void {
		this.statusBarItem.tooltip = tooltip;
	}

	public dispose(): void {
		this.statusBarItem.dispose();
	}
}

enum StatusIcon {
	busy = "$(sync~spin)",
	ready = "$(thumbsup)",
	error = "$(thumbsdown)",
	launching = "$(rocket)",
}

export const extensionStatusBar: ExtensionStatusBarProvider = new ExtensionStatusBarProvider();
