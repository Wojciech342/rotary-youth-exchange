import { type Coordinator } from './coordinator';

export const mockCoordinators: Coordinator[] = [
    {
        id: 1,
        name: 'Jan Kowalski',
        email: 'jan.kowalski@rotary.pl',
        phone: '+48 123 456 789',
        profile_picture: 'https://placehold.co/100x100/e2e8f0/333?text=JK',
        description: 'Main coordinator for District 2231. Focused on European and Asian camps.',
        district: 'District 2231',
        camps: [
        { id: 101, name: 'Discover Pacific Taiwan Cycling Camp', year: 2025 },
        { id: 102, name: 'Explore Egypt "Egyptology 2025" camp', year: 2025 },
        { id: 901, name: 'Discover Pacific Taiwan Cycling Camp', year: 2024 },
        ]
    },
    {
        id: 2,
        name: 'Marie Claire',
        email: 'marie.claire@rotary.fr',
        phone: '+33 456 789 123',
        profile_picture: 'https://placehold.co/100x100/e2e8f0/333?text=MC',
        description: 'Coordinator for Western France, specializing in sailing and coastal camps.',
        district: 'District 1700',
        camps: [
        { id: 201, name: 'French Riviera Sailing Adventure', year: 2025 }
        ]
    },
    {
        id: 3,
        name: 'Hans Gruber',
        email: 'hans.gruber@rotary.de',
        phone: '+49 789 123 456',
        profile_picture: 'https://placehold.co/100x100/e2e8f0/333?text=HG',
        description: 'Bavarian specialist. Loves mountains and traditional culture.',
        district: 'District 1841',
        camps: [
        { id: 202, name: 'Discover Enjoy Bavaria', year: 2025 }
        ]
    },
    {
        id: 4,
        name: 'Li Wang',
        email: 'li.wang@rotary.cn',
        phone: '+86 10 1234 5678',
        profile_picture: '',
        description: 'Coordinator for Eastern China.',
        district: 'District 3450',
        camps: [
        { id: 902, name: 'Mandarian and culture exploring', year: 2024 }
        ]
    }
];
