import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import BuildingResolve from './route/building-routing-resolve.service';

const buildingRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/building').then(m => m.Building),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/building-detail').then(m => m.BuildingDetail),
    resolve: {
      building: BuildingResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/building-update').then(m => m.BuildingUpdate),
    resolve: {
      building: BuildingResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/building-update').then(m => m.BuildingUpdate),
    resolve: {
      building: BuildingResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default buildingRoute;
