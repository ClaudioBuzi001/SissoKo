export interface Pizzeria {
  id: string;
  name: string;
  address: string;
  city: string;
  phoneNumber: string;
  openingHours: string;
  deliveryAvailable: boolean;
  latitude: number | null;
  longitude: number | null;
}

export type PizzeriaPayload = Omit<Pizzeria, 'id'>;
