import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ExamScheduleEntryResolve from './route/exam-schedule-entry-routing-resolve.service';

const examScheduleEntryRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/exam-schedule-entry').then(m => m.ExamScheduleEntry),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/exam-schedule-entry-detail').then(m => m.ExamScheduleEntryDetail),
    resolve: {
      examScheduleEntry: ExamScheduleEntryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/exam-schedule-entry-update').then(m => m.ExamScheduleEntryUpdate),
    resolve: {
      examScheduleEntry: ExamScheduleEntryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/exam-schedule-entry-update').then(m => m.ExamScheduleEntryUpdate),
    resolve: {
      examScheduleEntry: ExamScheduleEntryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default examScheduleEntryRoute;
