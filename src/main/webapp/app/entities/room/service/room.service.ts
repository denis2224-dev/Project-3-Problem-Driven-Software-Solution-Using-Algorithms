import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IRoom, NewRoom } from '../room.model';

export type PartialUpdateRoom = Partial<IRoom> & Pick<IRoom, 'id'>;

@Injectable()
export class RoomsService {
  readonly roomsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(undefined);
  readonly roomsResource = httpResource<IRoom[]>(() => {
    const params = this.roomsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of room that have been fetched. It is updated when the roomsResource emits a new value.
   * In case of error while fetching the rooms, the signal is set to an empty array.
   */
  readonly rooms = computed(() => (this.roomsResource.hasValue() ? this.roomsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/rooms');
}

@Injectable({ providedIn: 'root' })
export class RoomService extends RoomsService {
  protected readonly http = inject(HttpClient);

  create(room: NewRoom): Observable<IRoom> {
    return this.http.post<IRoom>(this.resourceUrl, room);
  }

  update(room: IRoom): Observable<IRoom> {
    return this.http.put<IRoom>(`${this.resourceUrl}/${encodeURIComponent(this.getRoomIdentifier(room))}`, room);
  }

  partialUpdate(room: PartialUpdateRoom): Observable<IRoom> {
    return this.http.patch<IRoom>(`${this.resourceUrl}/${encodeURIComponent(this.getRoomIdentifier(room))}`, room);
  }

  find(id: number): Observable<IRoom> {
    return this.http.get<IRoom>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IRoom[]>> {
    const options = createRequestOption(req);
    return this.http.get<IRoom[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getRoomIdentifier(room: Pick<IRoom, 'id'>): number {
    return room.id;
  }

  compareRoom(o1: Pick<IRoom, 'id'> | null, o2: Pick<IRoom, 'id'> | null): boolean {
    return o1 && o2 ? this.getRoomIdentifier(o1) === this.getRoomIdentifier(o2) : o1 === o2;
  }

  addRoomToCollectionIfMissing<Type extends Pick<IRoom, 'id'>>(
    roomCollection: Type[],
    ...roomsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const rooms: Type[] = roomsToCheck.filter(isPresent);
    if (rooms.length > 0) {
      const roomCollectionIdentifiers = roomCollection.map(roomItem => this.getRoomIdentifier(roomItem));
      const roomsToAdd = rooms.filter(roomItem => {
        const roomIdentifier = this.getRoomIdentifier(roomItem);
        if (roomCollectionIdentifiers.includes(roomIdentifier)) {
          return false;
        }
        roomCollectionIdentifiers.push(roomIdentifier);
        return true;
      });
      return [...roomsToAdd, ...roomCollection];
    }
    return roomCollection;
  }
}
