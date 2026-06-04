import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import StudentGroupResolve from './route/student-group-routing-resolve.service';

const studentGroupRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/student-group').then(m => m.StudentGroup),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/student-group-detail').then(m => m.StudentGroupDetail),
    resolve: {
      studentGroup: StudentGroupResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/student-group-update').then(m => m.StudentGroupUpdate),
    resolve: {
      studentGroup: StudentGroupResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/student-group-update').then(m => m.StudentGroupUpdate),
    resolve: {
      studentGroup: StudentGroupResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default studentGroupRoute;
