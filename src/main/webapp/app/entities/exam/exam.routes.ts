import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ExamResolve from './route/exam-routing-resolve.service';

const examRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/exam').then(m => m.Exam),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/exam-detail').then(m => m.ExamDetail),
    resolve: {
      exam: ExamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/exam-update').then(m => m.ExamUpdate),
    resolve: {
      exam: ExamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/exam-update').then(m => m.ExamUpdate),
    resolve: {
      exam: ExamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default examRoute;
