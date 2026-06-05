import { NgClass } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { catchError, map, of } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';

interface TimetableVersionOption {
  id: number;
  versionNumber?: number | null;
  createdAt?: string | null;
  totalHardConflicts?: number | null;
  totalSoftPenalty?: number | null;
  timetable?: {
    id: number;
    name?: string | null;
    semester?: string | null;
    academicYear?: string | null;
  } | null;
}

interface TimetableEntryView {
  timetableEntryId: number;
  courseCode?: string | null;
  courseName?: string | null;
  eventType?: string | null;
  professorName?: string | null;
  studentGroupName?: string | null;
  roomCode?: string | null;
  roomName?: string | null;
  buildingCode?: string | null;
  buildingName?: string | null;
  dayOfWeek?: string | null;
  startTime?: string | null;
  endTime?: string | null;
}

interface TimeRow {
  key: string;
  startTime: string;
  endTime: string;
}

@Component({
  selector: 'jhi-timetable-view',
  templateUrl: './timetable-view.html',
  styleUrl: './timetable-view.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NgClass, RouterLink],
})
export default class TimetableView implements OnInit {
  readonly days = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
  readonly versions = signal<TimetableVersionOption[]>([]);
  readonly selectedVersionId = signal<number | null>(null);
  readonly entries = signal<TimetableEntryView[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly actionMessage = signal<string | null>(null);

  readonly selectedVersion = computed(() => this.versions().find(version => version.id === this.selectedVersionId()) ?? null);
  readonly timeRows = computed<TimeRow[]>(() => {
    const rows = new Map<string, TimeRow>();
    for (const entry of this.entries()) {
      if (!entry.startTime || !entry.endTime) {
        continue;
      }
      const key = `${entry.startTime}-${entry.endTime}`;
      rows.set(key, { key, startTime: entry.startTime, endTime: entry.endTime });
    }
    return [...rows.values()].sort((left, right) => left.startTime.localeCompare(right.startTime));
  });

  private readonly http = inject(HttpClient);
  private readonly applicationConfigService = inject(ApplicationConfigService);

  ngOnInit(): void {
    this.loadVersions();
  }

  loadVersions(): void {
    this.loading.set(true);
    this.error.set(null);
    this.actionMessage.set(null);
    this.http
      .get<TimetableVersionOption[]>(this.applicationConfigService.getEndpointFor('api/timetable-versions'), {
        params: { page: 0, size: 20, sort: 'createdAt,desc', eagerload: true },
      })
      .pipe(
        catchError(() => {
          this.error.set('Timetable versions could not be loaded. Generate a timetable first, then refresh this page.');
          return of([]);
        }),
      )
      .subscribe(versions => {
        this.versions.set(versions);
        const selectedId = versions[0]?.id ?? null;
        this.selectedVersionId.set(selectedId);
        if (selectedId) {
          this.loadEntries(selectedId);
        } else {
          this.entries.set([]);
          this.loading.set(false);
        }
      });
  }

  selectVersion(event: Event): void {
    const value = Number((event.target as HTMLSelectElement).value);
    this.selectedVersionId.set(Number.isNaN(value) ? null : value);
    if (!Number.isNaN(value)) {
      this.loadEntries(value);
    }
  }

  entriesFor(day: string, row: TimeRow): TimetableEntryView[] {
    return this.entries().filter(entry => entry.dayOfWeek === day && entry.startTime === row.startTime && entry.endTime === row.endTime);
  }

  eventTypeClass(eventType: string | null | undefined): string {
    return (eventType ?? 'EVENT').toLowerCase();
  }

  formatDay(day: string): string {
    return day.charAt(0) + day.slice(1).toLowerCase();
  }

  fullEntryTitle(entry: TimetableEntryView): string {
    return [entry.courseCode, entry.courseName, entry.professorName, entry.studentGroupName, entry.roomCode, entry.buildingCode]
      .filter(Boolean)
      .join(' | ');
  }

  downloadCsv(versionId: number): void {
    this.error.set(null);
    this.actionMessage.set(null);
    this.http
      .get(this.applicationConfigService.getEndpointFor(`api/timetable-versions/${versionId}/export/csv`), { responseType: 'blob' })
      .pipe(
        catchError(() => {
          this.error.set('CSV export failed for the selected timetable version.');
          return of(null);
        }),
      )
      .subscribe(blob => {
        if (!blob) {
          return;
        }
        const downloadUrl = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = downloadUrl;
        link.download = `timetable-version-${versionId}.csv`;
        link.click();
        URL.revokeObjectURL(downloadUrl);
        this.actionMessage.set('CSV export downloaded.');
      });
  }

  approveTimetable(timetableId: number | null | undefined): void {
    this.updateTimetableStatus(timetableId, 'approve');
  }

  publishTimetable(timetableId: number | null | undefined): void {
    this.updateTimetableStatus(timetableId, 'publish');
  }

  private loadEntries(versionId: number): void {
    this.loading.set(true);
    this.error.set(null);
    this.http
      .get<TimetableEntryView[]>(this.applicationConfigService.getEndpointFor(`api/timetable-versions/${versionId}/entries`))
      .pipe(
        map(entries => [...entries].sort(this.compareEntries)),
        catchError(() => {
          this.error.set('Timetable entries could not be loaded for the selected version.');
          return of([]);
        }),
      )
      .subscribe(entries => {
        this.entries.set(entries);
        this.loading.set(false);
      });
  }

  private updateTimetableStatus(timetableId: number | null | undefined, action: 'approve' | 'publish'): void {
    if (!timetableId) {
      this.error.set('The selected version is not linked to a timetable.');
      return;
    }
    this.error.set(null);
    this.actionMessage.set(null);
    this.http
      .post(this.applicationConfigService.getEndpointFor(`api/timetables/${timetableId}/${action}`), null)
      .pipe(
        catchError(() => {
          this.error.set(`Timetable ${action} failed. Sign in as admin and try again.`);
          return of(null);
        }),
      )
      .subscribe(result => {
        if (!result) {
          return;
        }
        this.actionMessage.set(`Timetable ${action} completed.`);
        this.loadVersions();
      });
  }

  private compareEntries(left: TimetableEntryView, right: TimetableEntryView): number {
    return `${left.dayOfWeek ?? ''}-${left.startTime ?? ''}-${left.courseCode ?? ''}`.localeCompare(
      `${right.dayOfWeek ?? ''}-${right.startTime ?? ''}-${right.courseCode ?? ''}`,
    );
  }
}
