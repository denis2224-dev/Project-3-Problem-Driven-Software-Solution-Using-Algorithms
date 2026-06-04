import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ScheduleConflictResolve from './route/schedule-conflict-routing-resolve.service';

const scheduleConflictRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/schedule-conflict').then(m => m.ScheduleConflict),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/schedule-conflict-detail').then(m => m.ScheduleConflictDetail),
    resolve: {
      scheduleConflict: ScheduleConflictResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/schedule-conflict-update').then(m => m.ScheduleConflictUpdate),
    resolve: {
      scheduleConflict: ScheduleConflictResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/schedule-conflict-update').then(m => m.ScheduleConflictUpdate),
    resolve: {
      scheduleConflict: ScheduleConflictResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default scheduleConflictRoute;
