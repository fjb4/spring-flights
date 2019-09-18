import {RadarMap} from './map';

const startCoordinates = [45.757237, 4.832147];
const zoomLevel = 10;


window.onload = function init() {
    let map = new RadarMap(startCoordinates, zoomLevel);
};


