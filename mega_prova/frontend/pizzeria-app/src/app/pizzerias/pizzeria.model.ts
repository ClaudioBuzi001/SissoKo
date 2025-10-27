export interface Pizzeria {
  id: string;
  name: string;
  address: string;
  city: string;
  phoneNumber: string;
  openingHours: string;
  deliveryAvailable: boolean;
}

export type PizzeriaPayload = Omit<Pizzeria, 'id'>;
