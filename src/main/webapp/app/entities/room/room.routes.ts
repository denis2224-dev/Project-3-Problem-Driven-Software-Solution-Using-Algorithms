import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import RoomResolve from './route/room-routing-resolve.service';

const roomRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/room').then(m => m.Room),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/room-detail').then(m => m.RoomDetail),
    resolve: {
      room: RoomResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/room-update').then(m => m.RoomUpdate),
    resolve: {
      room: RoomResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/room-update').then(m => m.RoomUpdate),
    resolve: {
      room: RoomResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default roomRoute;
