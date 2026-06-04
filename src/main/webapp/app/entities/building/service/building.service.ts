import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IBuilding, NewBuilding } from '../building.model';

export type PartialUpdateBuilding = Partial<IBuilding> & Pick<IBuilding, 'id'>;

@Injectable()
export class BuildingsService {
  readonly buildingsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly buildingsResource = httpResource<IBuilding[]>(() => {
    const params = this.buildingsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of building that have been fetched. It is updated when the buildingsResource emits a new value.
   * In case of error while fetching the buildings, the signal is set to an empty array.
   */
  readonly buildings = computed(() => (this.buildingsResource.hasValue() ? this.buildingsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/buildings');
}

@Injectable({ providedIn: 'root' })
export class BuildingService extends BuildingsService {
  protected readonly http = inject(HttpClient);

  create(building: NewBuilding): Observable<IBuilding> {
    return this.http.post<IBuilding>(this.resourceUrl, building);
  }

  update(building: IBuilding): Observable<IBuilding> {
    return this.http.put<IBuilding>(`${this.resourceUrl}/${encodeURIComponent(this.getBuildingIdentifier(building))}`, building);
  }

  partialUpdate(building: PartialUpdateBuilding): Observable<IBuilding> {
    return this.http.patch<IBuilding>(`${this.resourceUrl}/${encodeURIComponent(this.getBuildingIdentifier(building))}`, building);
  }

  find(id: number): Observable<IBuilding> {
    return this.http.get<IBuilding>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IBuilding[]>> {
    const options = createRequestOption(req);
    return this.http.get<IBuilding[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getBuildingIdentifier(building: Pick<IBuilding, 'id'>): number {
    return building.id;
  }

  compareBuilding(o1: Pick<IBuilding, 'id'> | null, o2: Pick<IBuilding, 'id'> | null): boolean {
    return o1 && o2 ? this.getBuildingIdentifier(o1) === this.getBuildingIdentifier(o2) : o1 === o2;
  }

  addBuildingToCollectionIfMissing<Type extends Pick<IBuilding, 'id'>>(
    buildingCollection: Type[],
    ...buildingsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const buildings: Type[] = buildingsToCheck.filter(isPresent);
    if (buildings.length > 0) {
      const buildingCollectionIdentifiers = buildingCollection.map(buildingItem => this.getBuildingIdentifier(buildingItem));
      const buildingsToAdd = buildings.filter(buildingItem => {
        const buildingIdentifier = this.getBuildingIdentifier(buildingItem);
        if (buildingCollectionIdentifiers.includes(buildingIdentifier)) {
          return false;
        }
        buildingCollectionIdentifiers.push(buildingIdentifier);
        return true;
      });
      return [...buildingsToAdd, ...buildingCollection];
    }
    return buildingCollection;
  }
}
