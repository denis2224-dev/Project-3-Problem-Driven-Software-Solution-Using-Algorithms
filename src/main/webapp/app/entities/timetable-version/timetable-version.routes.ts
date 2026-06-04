import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import TimetableVersionResolve from './route/timetable-version-routing-resolve.service';

const timetableVersionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/timetable-version').then(m => m.TimetableVersion),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/timetable-version-detail').then(m => m.TimetableVersionDetail),
    resolve: {
      timetableVersion: TimetableVersionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/timetable-version-update').then(m => m.TimetableVersionUpdate),
    resolve: {
      timetableVersion: TimetableVersionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/timetable-version-update').then(m => m.TimetableVersionUpdate),
    resolve: {
      timetableVersion: TimetableVersionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default timetableVersionRoute;
