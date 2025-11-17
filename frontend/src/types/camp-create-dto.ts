export interface CampCreateDto {
    name: string;
    description: string;
    country: string;
    date_start: string;
    date_end: string;
    age_min: number;
    age_max: number;
    price: number;
    status: 'OPEN';
    entire_limit: number;
    male_limit: number;
    female_limit: number;
    limit_per_country: number;
}
