import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import QRCode from 'qrcode';
import { environment } from 'environments/environment';

interface DemoCredential {
  label: string;
  username: string;
  password: string;
  note: string;
}

@Component({
  selector: 'jhi-mobile-demo',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink],
  templateUrl: './mobile-demo.html',
})
export default class MobileDemo implements OnInit {
  readonly configuredDemoUrl = environment.PUBLIC_DEMO_URL.trim();
  readonly demoUrl = this.configuredDemoUrl || this.currentOrigin();
  readonly usesConfiguredUrl = this.configuredDemoUrl.length > 0;
  readonly qrCodeDataUrl = signal('');
  readonly qrCodeError = signal(false);
  readonly copied = signal(false);

  readonly credentials: DemoCredential[] = [
    {
      label: 'Read-only demo user',
      username: 'user',
      password: 'user',
      note: 'Use this account to explore the demo safely.',
    },
    {
      label: 'Admin demo account',
      username: 'admin',
      password: 'admin',
      note: 'Use only when loading demo data or generating schedules.',
    },
  ];

  ngOnInit(): void {
    void this.generateQrCode();
  }

  async copyDemoUrl(): Promise<void> {
    try {
      await navigator.clipboard.writeText(this.demoUrl);
      this.copied.set(true);
      window.setTimeout(() => this.copied.set(false), 1800);
    } catch {
      this.copied.set(false);
    }
  }

  private async generateQrCode(): Promise<void> {
    try {
      this.qrCodeDataUrl.set(
        await QRCode.toDataURL(this.demoUrl, {
          width: 240,
          margin: 1,
          color: {
            dark: '#0b0b16',
            light: '#f8fafc',
          },
          errorCorrectionLevel: 'M',
        }),
      );
    } catch {
      this.qrCodeError.set(true);
    }
  }

  private currentOrigin(): string {
    return window.location.origin;
  }
}
