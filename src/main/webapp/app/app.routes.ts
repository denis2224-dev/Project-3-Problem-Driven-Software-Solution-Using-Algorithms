import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { Authority } from 'app/shared/jhipster/constants';

import { errorRoute } from './layouts/error/error.route';

const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./landing/landing'),
    title: 'UniScheduler',
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./home/home'),
    title: 'Dashboard',
    canActivate: [UserRouteAccessService],
  },
  {
    path: '',
    loadComponent: () => import('./layouts/navbar/navbar'),
    outlet: 'navbar',
  },
  {
    path: 'admin',
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./admin/admin.routes'),
  },
  {
    path: 'account',
    loadChildren: () => import('./account/account.route'),
  },
  {
    path: 'login',
    loadComponent: () => import('./login/login'),
    title: 'login.title',
  },
  {
    path: 'mobile-demo',
    loadComponent: () => import('./mobile-demo/mobile-demo'),
    title: 'Mobile QR Demo',
  },
  {
    path: 'algorithms',
    loadComponent: () => import('./algorithm-explanation/algorithm-explanation'),
    title: 'Algorithm Explanation',
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'demo-scenario',
    loadComponent: () => import('./demo-scenario/demo-scenario'),
    title: 'FAF Demo Scenario',
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'exam-schedule',
    loadComponent: () => import('./exam-schedule/exam-schedule'),
    title: 'Exam Schedule',
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'timetable-view',
    loadComponent: () => import('./timetable-view/timetable-view'),
    title: 'Weekly Timetable View',
    canActivate: [UserRouteAccessService],
  },
  {
    path: '',
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./entities/entity.routes'),
  },
  ...errorRoute,
];

export default routes;
