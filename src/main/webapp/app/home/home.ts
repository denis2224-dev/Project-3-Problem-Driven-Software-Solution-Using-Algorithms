import { NgClass } from '@angular/common';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, effect, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { catchError, forkJoin, map, Observable, of } from 'rxjs';

import { AccountService } from 'app/core/auth/account.service';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { Authority } from 'app/shared/jhipster/constants';

interface DashboardMetric {
  label: string;
  value: number;
  detail: string;
  route: string;
}

interface DashboardSolverJob {
  id: number;
  status?: string | null;
  progressPercent?: number | null;
  hardConflictCount?: number | null;
  softPenaltyScore?: number | null;
  backtrackCount?: number | null;
  domainReductionCount?: number | null;
  runtimeMs?: number | null;
  message?: string | null;
}

@Component({
  selector: 'jhi-home',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './home.html',
  styleUrl: './home.scss',
  imports: [NgClass, RouterLink],
})
export default class Home {
  public readonly account = inject(AccountService).account;
  readonly isAdmin = computed(() => this.account()?.authorities.includes(Authority.ADMIN) ?? false);

  readonly metrics = signal<DashboardMetric[]>([
    { label: 'Course events', value: 0, detail: 'Lectures, laboratories, seminars', route: '/course-event' },
    { label: 'Professors', value: 0, detail: 'Teaching staff with constraints', route: '/professor' },
    { label: 'Rooms', value: 0, detail: 'Lecture, lab, seminar, exam rooms', route: '/room' },
  ]);
  readonly latestJob = signal<DashboardSolverJob | null>(null);
  readonly loading = signal(false);
  readonly dashboardError = signal<string | null>(null);

  private dashboardLoaded = false;
  private readonly router = inject(Router);
  private readonly http = inject(HttpClient);
  private readonly applicationConfigService = inject(ApplicationConfigService);

  constructor() {
    effect(() => {
      if (this.account() && !this.dashboardLoaded) {
        this.dashboardLoaded = true;
        this.loadDashboard();
      }
    });
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  loadDashboard(): void {
    this.loading.set(true);
    this.dashboardError.set(null);

    forkJoin({
      courseEvents: this.countResource('api/course-events'),
      professors: this.countResource('api/professors'),
      rooms: this.countResource('api/rooms'),
      latestJob: this.latestSolverJob(),
    }).subscribe({
      next: result => {
        this.metrics.set([
          { ...this.metrics()[0], value: result.courseEvents },
          { ...this.metrics()[1], value: result.professors },
          { ...this.metrics()[2], value: result.rooms },
        ]);
        this.latestJob.set(result.latestJob);
        this.loading.set(false);
      },
      error: () => {
        this.dashboardError.set('Dashboard data could not be loaded.');
        this.loading.set(false);
      },
    });
  }

  statusClass(status: string | null | undefined): string {
    return (status ?? 'NO_JOB').toLowerCase().replaceAll('_', '-');
  }

  private countResource(endpoint: string): Observable<number> {
    return this.http
      .get<unknown[]>(this.applicationConfigService.getEndpointFor(endpoint), {
        params: { page: 0, size: 1 },
        observe: 'response',
      })
      .pipe(
        map((response: HttpResponse<unknown[]>) => Number(response.headers.get('X-Total-Count') ?? response.body?.length ?? 0)),
        catchError(() => of(0)),
      );
  }

  private latestSolverJob(): Observable<DashboardSolverJob | null> {
    return this.http
      .get<DashboardSolverJob[]>(this.applicationConfigService.getEndpointFor('api/solver-jobs'), {
        params: { page: 0, size: 1, sort: 'id,desc' },
        observe: 'response',
      })
      .pipe(
        map(response => response.body?.[0] ?? null),
        catchError(() => of(null)),
      );
  }
}
