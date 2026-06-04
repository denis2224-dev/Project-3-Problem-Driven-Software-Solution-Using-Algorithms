import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import TimeslotResolve from './route/timeslot-routing-resolve.service';

const timeslotRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/timeslot').then(m => m.Timeslot),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/timeslot-detail').then(m => m.TimeslotDetail),
    resolve: {
      timeslot: TimeslotResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/timeslot-update').then(m => m.TimeslotUpdate),
    resolve: {
      timeslot: TimeslotResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/timeslot-update').then(m => m.TimeslotUpdate),
    resolve: {
      timeslot: TimeslotResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default timeslotRoute;
