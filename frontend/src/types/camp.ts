import { type Coordinator } from "./coordinator";

export interface Camp {
    id: number;
    name: string;
    description: string;
    country: string;
    coordinator: Coordinator;
    date_start: string;
    date_end: string;
    age_min: number;
    age_max: number;
    price: number;
    status: 'OPEN' | 'CLOSED' | 'CANCELLED';
    flyer_pdf?: string;
    image?: string;
    entire_limit: number;
    male_limit: number;
    female_limit: number;
    limit_per_country: number;
}
