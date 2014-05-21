app.Config = function() {

	function Location(params) {
		this.bounds = params.bounds;
		this.jsonUrl = params.jsonUrl;
		this.zoom = params.zoom;
		this.getCenter();
	}
	Location.prototype.getCenter = function() {
		if (!this.center) {
			this.center = {
				lat: this.bounds.sud + ((this.bounds.north - this.bounds.sud) / 2),
				lng: this.bounds.west + ((this.bounds.est - this.bounds.west) / 2 )
			};
		}
		return this.center;
	};

	return {
		defaultLoc: 'athens_1',
		key: {
			// not used any more
			leaflet: '5f9ebf625acb4df3a40163ddca8c064b'
		},
		locations: {
			athens_1: new Location({
				jsonUrl: 'data/athens/clustering-output-eps0.039283710065919304.geojson',
				bounds: {
					north: 38.076203576652,
					sud: 37.9323527,
					west: 23.628845214844,
					est: 23.8113263
				},
				zoom: 11
			}),
			athens_2: new Location({
				jsonUrl: 'data/athens/clustering-output-eps0.05.geojson',
				bounds: {
					north: 38.076203576652,
					sud: 37.9323527,
					west: 23.628845214844,
					est: 23.8113263
				},
				zoom: 11
			}),	
			athens_3: new Location({
				jsonUrl: 'data/athens/clustering-output-eps0.06.geojson',
				bounds: {
					north: 38.076203576652,
					sud: 37.9323527,
					west: 23.628845214844,
					est: 23.8113263
				},
				zoom: 11
			}),
			athens_4: new Location({
				jsonUrl: 'data/athens/clustering-output-eps0.07.geojson',
				bounds: {
					north: 38.076203576652,
					sud: 37.9323527,
					west: 23.628845214844,
					est: 23.8113263
				},
				zoom: 11
			}),						
			athens_5: new Location({
				jsonUrl: 'data/athens/clustering-output-eps0.08.geojson',
				bounds: {
					north: 38.076203576652,
					sud: 37.9323527,
					west: 23.628845214844,
					est: 23.8113263
				},
				zoom: 11
			})/*,
			heraklion: new Location({
				jsonUrl: 'data/heraklion/clustering-output-eps0.08838834764831845.geojson',
				bounds: {
					north: 35.344815237811,
					sud:  35.2773249,
					west: 25.109596252441,
					est: 25.192337036133
				},
				zoom: 11
			}),
			heraklionOpt: new Location({
				jsonUrl: 'data/heraklion/opt-clustering-output-eps0.08838834764831845.geojson',
				bounds: {
					north: 35.344815237811,
					sud:  35.2773249,
					west: 25.109596252441,
					est: 25.192337036133
				},
				zoom: 11
			}),			
			london02: new Location({
				jsonUrl: 'data/london/clustering-output-eps0.02525381361380527.geojson',
				bounds: {
					north: 51.58389660297623,
					sud:  51.3591295,
					west: -0.35980224609375,
					est: 0.0019242
				},
				zoom: 11
			}),
			london04: new Location({
				jsonUrl: 'data/london/clustering-output-eps0.04.geojson',
				bounds: {
					north: 51.58389660297623,
					sud:  51.3591295,
					west: -0.35980224609375,
					est: 0.0019242
				},
				zoom: 11
			}),			
			london08: new Location({
				jsonUrl: 'data/london/clustering-output-eps0.08.geojson',
				bounds: {
					north: 51.58389660297623,
					sud:  51.3591295,
					west: -0.35980224609375,
					est: 0.0019242
				},
				zoom: 11
			}),
			milan01: new Location({
				jsonUrl: 'data/milan/clustering-output-eps0.014142135623730952.geojson',
				bounds: {
					north: 45.567794914783256,
					sud:  45.35668565341512,
					west: 9.011490619692509,
					est: 9.312688264185255
				},
				zoom: 11
			}),		
			milan02: new Location({
				jsonUrl: 'data/milan/clustering-output-eps0.02.geojson',
				bounds: {
					north: 45.567794914783256,
					sud:  45.35668565341512,
					west: 9.011490619692509,
					est: 9.312688264185255
				},
				zoom: 11
			}),		
			milan03: new Location({
				jsonUrl: 'data/milan/clustering-output-eps0.02.geojson',
				bounds: {
					north: 45.567794914783256,
					sud:  45.35668565341512,
					west: 9.011490619692509,
					est: 9.312688264185255
				},
				zoom: 11
			}),								
			turin07: new Location({
				jsonUrl: 'data/turin/clustering-output-eps0.07071067811865477.geojson',
				bounds: {
					north: 45.10975600522702,
					sud: 45.04393354716772,
					west: 7.630176544189453,
					est: 7.734889984130859
				},
				zoom: 11
			}),
			turin14: new Location({
				jsonUrl: 'data/turin/clustering-output-eps0.14.geojson',
				bounds: {
					north: 45.10975600522702,
					sud: 45.04393354716772,
					west: 7.630176544189453,
					est: 7.734889984130859
				},
				zoom: 11
			}),			
			turin28: new Location({
				jsonUrl: 'data/turin/clustering-output-eps0.28.geojson',
				bounds: {
					north: 45.10975600522702,
					sud: 45.04393354716772,
					west: 7.630176544189453,
					est: 7.734889984130859
				},
				zoom: 11
			}),			
			trentino: new Location({
				jsonUrl: 'data/trentino/clustering-output-eps0.09.geojson',
				bounds: {
					north: 46.53633684901995,
					sud: 45.672795442796335,
					west: 10.914315493899755,
					est: 11.831262564937385
				},
				zoom: 11
			})*/
		}
	};
};