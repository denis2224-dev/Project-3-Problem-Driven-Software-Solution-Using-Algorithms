import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import TimetableResolve from './route/timetable-routing-resolve.service';

const timetableRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/timetable').then(m => m.Timetable),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/timetable-detail').then(m => m.TimetableDetail),
    resolve: {
      timetable: TimetableResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/timetable-update').then(m => m.TimetableUpdate),
    resolve: {
      timetable: TimetableResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/timetable-update').then(m => m.TimetableUpdate),
    resolve: {
      timetable: TimetableResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default timetableRoute;
