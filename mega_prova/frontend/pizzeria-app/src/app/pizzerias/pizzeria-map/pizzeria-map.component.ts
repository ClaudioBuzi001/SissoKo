import { NgIf } from '@angular/common';
import { Component, Input, OnChanges } from '@angular/core';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import * as L from 'leaflet';
import { divIcon, latLng, LatLngBounds, Layer, marker, tileLayer } from 'leaflet';

import { Pizzeria } from '../pizzeria.model';

const neonMarker = divIcon({
	className: 'neon-marker',
	html: '<span class="neon-marker__glow"></span><span class="neon-marker__core"></span>',
	iconSize: [32, 32],
	iconAnchor: [16, 16],
	popupAnchor: [0, -20]
});

const FALLBACK_BOUNDS = L.latLngBounds([35.0, 6.0], [47.5, 18.5]);

@Component({
  selector: 'app-pizzeria-map',
  standalone: true,
  imports: [LeafletModule, NgIf],
  templateUrl: './pizzeria-map.component.html',
  styleUrls: ['./pizzeria-map.component.scss']
})
export class PizzeriaMapComponent implements OnChanges {
  @Input() pizzerias: Pizzeria[] = [];

  readonly options = {
	maxZoom: 18,
	minZoom: 3,
	layers: [
		tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
			attribution: '&copy; OpenStreetMap contributors &copy; CARTO',
			detectRetina: true
		})
	],
	zoom: 6,
	center: latLng(41.8719, 12.5674) // centro Italia
};

  layers: Layer[] = [];
  fitBounds: LatLngBounds = FALLBACK_BOUNDS;
  hasMarkers = false;

  ngOnChanges(): void {
    this.refreshMarkers();
  }

  private refreshMarkers(): void {
    const markers = this.pizzerias
      .filter((pizzeria) => this.hasCoordinates(pizzeria))
      .map((pizzeria) =>
        marker([pizzeria.latitude as number, pizzeria.longitude as number], {
          title: pizzeria.name,
          icon: neonMarker
        }).bindPopup(this.buildPopupContent(pizzeria), { closeButton: false })
      );

    this.layers = markers;
    this.hasMarkers = markers.length > 0;

    this.fitBounds = this.hasMarkers
      ? L.latLngBounds(markers.map((layer) => (layer as L.Marker).getLatLng())).pad(0.2)
      : FALLBACK_BOUNDS;
  }

  private hasCoordinates(pizzeria: Pizzeria): boolean {
    return typeof pizzeria.latitude === 'number' && typeof pizzeria.longitude === 'number';
  }

  private buildPopupContent(pizzeria: Pizzeria): string {
    return `<strong>${pizzeria.name}</strong><br/>
      ${pizzeria.address}<br/>
      ${pizzeria.city}<br/>
      ${pizzeria.phoneNumber}`;
  }
}
