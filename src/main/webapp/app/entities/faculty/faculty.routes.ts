import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import FacultyResolve from './route/faculty-routing-resolve.service';

const facultyRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/faculty').then(m => m.Faculty),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/faculty-detail').then(m => m.FacultyDetail),
    resolve: {
      faculty: FacultyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/faculty-update').then(m => m.FacultyUpdate),
    resolve: {
      faculty: FacultyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/faculty-update').then(m => m.FacultyUpdate),
    resolve: {
      faculty: FacultyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default facultyRoute;
