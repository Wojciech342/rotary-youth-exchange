import { type Coordinator } from "./coordinator";

export interface Camp {
    id: number;
    name: string;
    description: string;
    country: string;
    coordinator: Coordinator;
    date_start: string;
    date_end: string;
    age_min: string;
    age_max: string;
    price: number;
    status: 'OPEN' | 'CLOSED' | 'CANCELLED';
    flyer_pdf?: string;
    image?: string;
    entire_limit: number;
    male_limit: number;
    female_limit: number;
}
