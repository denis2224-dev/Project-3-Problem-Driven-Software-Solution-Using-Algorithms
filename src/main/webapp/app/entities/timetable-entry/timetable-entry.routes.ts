import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import TimetableEntryResolve from './route/timetable-entry-routing-resolve.service';

const timetableEntryRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/timetable-entry').then(m => m.TimetableEntry),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/timetable-entry-detail').then(m => m.TimetableEntryDetail),
    resolve: {
      timetableEntry: TimetableEntryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/timetable-entry-update').then(m => m.TimetableEntryUpdate),
    resolve: {
      timetableEntry: TimetableEntryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/timetable-entry-update').then(m => m.TimetableEntryUpdate),
    resolve: {
      timetableEntry: TimetableEntryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default timetableEntryRoute;
