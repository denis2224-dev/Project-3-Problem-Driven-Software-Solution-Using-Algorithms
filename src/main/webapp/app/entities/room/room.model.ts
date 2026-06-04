import { IBuilding } from 'app/entities/building/building.model';
import { RoomType } from 'app/entities/enumerations/room-type.model';

export interface IRoom {
  id: number;
  name?: string | null;
  code?: string | null;
  capacity?: number | null;
  roomType?: keyof typeof RoomType | null;
  equipment?: string | null;
  building?: Pick<IBuilding, 'id' | 'name'> | null;
}

export type NewRoom = Omit<IRoom, 'id'> & { id: null };
