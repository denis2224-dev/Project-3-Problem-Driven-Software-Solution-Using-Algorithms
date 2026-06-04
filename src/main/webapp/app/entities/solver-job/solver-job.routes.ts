import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import SolverJobResolve from './route/solver-job-routing-resolve.service';

const solverJobRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/solver-job').then(m => m.SolverJob),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/solver-job-detail').then(m => m.SolverJobDetail),
    resolve: {
      solverJob: SolverJobResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/solver-job-update').then(m => m.SolverJobUpdate),
    resolve: {
      solverJob: SolverJobResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/solver-job-update').then(m => m.SolverJobUpdate),
    resolve: {
      solverJob: SolverJobResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default solverJobRoute;
