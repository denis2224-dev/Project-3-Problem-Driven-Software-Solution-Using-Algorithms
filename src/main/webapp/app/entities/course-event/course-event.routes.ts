import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import CourseEventResolve from './route/course-event-routing-resolve.service';

const courseEventRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/course-event').then(m => m.CourseEvent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/course-event-detail').then(m => m.CourseEventDetail),
    resolve: {
      courseEvent: CourseEventResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/course-event-update').then(m => m.CourseEventUpdate),
    resolve: {
      courseEvent: CourseEventResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/course-event-update').then(m => m.CourseEventUpdate),
    resolve: {
      courseEvent: CourseEventResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default courseEventRoute;
