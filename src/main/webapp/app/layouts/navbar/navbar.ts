import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap/collapse';
import { NgbDropdown, NgbDropdownMenu, NgbDropdownToggle } from '@ng-bootstrap/ng-bootstrap/dropdown';
import { environment } from 'environments/environment';

import { AccountService } from 'app/core/auth/account.service';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { LoginService } from 'app/login/login.service';

@Component({
  selector: 'jhi-navbar',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
  imports: [RouterLink, RouterLinkActive, FontAwesomeModule, NgbCollapse, NgbDropdown, NgbDropdownMenu, NgbDropdownToggle],
})
export default class Navbar implements OnInit {
  readonly inProduction = signal(true);
  readonly isNavbarCollapsed = signal(true);
  readonly openAPIEnabled = signal(false);
  readonly version: string;
  readonly account = inject(AccountService).account;

  private readonly loginService = inject(LoginService);
  private readonly profileService = inject(ProfileService);
  private readonly router = inject(Router);

  constructor() {
    const { VERSION } = environment;
    const normalizedVersion = VERSION.trim();
    if (normalizedVersion && normalizedVersion.toLowerCase() !== 'unknown') {
      this.version = normalizedVersion.toLowerCase().startsWith('v') ? normalizedVersion : `v${normalizedVersion}`;
    } else {
      this.version = 'v1.0';
    }
  }

  ngOnInit(): void {
    this.profileService.getProfileInfo().subscribe(profileInfo => {
      this.inProduction.set(profileInfo.inProduction ?? true);
      this.openAPIEnabled.set(profileInfo.openAPIEnabled ?? false);
    });
  }

  collapseNavbar(): void {
    this.isNavbarCollapsed.set(true);
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  logout(): void {
    this.collapseNavbar();
    this.loginService.logout();
    this.router.navigate(['']);
  }
}
