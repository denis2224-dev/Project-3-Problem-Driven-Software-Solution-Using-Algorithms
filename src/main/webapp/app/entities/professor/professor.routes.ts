import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ProfessorResolve from './route/professor-routing-resolve.service';

const professorRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/professor').then(m => m.Professor),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/professor-detail').then(m => m.ProfessorDetail),
    resolve: {
      professor: ProfessorResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/professor-update').then(m => m.ProfessorUpdate),
    resolve: {
      professor: ProfessorResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/professor-update').then(m => m.ProfessorUpdate),
    resolve: {
      professor: ProfessorResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default professorRoute;
