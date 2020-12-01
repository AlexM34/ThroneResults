/* cvi_map_lib.js 2.41 (25-Aug-2011) (c) by Christian Effenberger. All Rights Reserved. Source: mapper.netzgesta.de. Distributed under Netzgestade Software License Agreement. Read more at... http://www.netzgesta.de/cvi/LICENSE.html. Commercial licenses available via... cvi[at]netzgesta[dot]de */
/* (13-Mar-2012) modified by THRONEMASTER.NET - The Boardgame Network -> IE fix + full flexibility to highlight multiple areas with different colors and opacity ;-P */

var canvascheck = document.createElement('canvas'),
    isIE = window.navigator.systemLanguage && (!document.documentMode || document.documentMode < 9) ? 1 : 0,
    isI8 = isIE && document.documentMode ? 1 : 0,
    isVM = isIE && document.namespaces ? 1 : 0,
    isCV = canvascheck.getContext ? 1 : 0,
    isJG = 0,
    jg = [];

// function showCoords(map, ele, x, y, w, h) {}

function getClassValue(classes, string) {
    var temp = 0,
        pos = string.length;
    for (var j = 0; j < classes.length; j++) {
        if (classes[j].indexOf(string) == 0) {
            temp = Math.min(classes[j].substring(pos), 100);
            break
        }
    }
    return Math.max(0, temp)
}

function getClassRGBColor(classes, string, color) {
    var temp, val = color,
        pos = string.length;
    for (var j = 0; j < classes.length; j++) {
        if (classes[j].indexOf(string) == 0) {
            temp = classes[j].substring(pos);
            val = temp.toLowerCase();
            break
        }
    }
    if (!val.match(/^[0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f]$/i)) {
        val = color || '000000'
    }
    if (!isCV) {
        return val
    } else {
        function hex2dec(hex) {
            return (Math.max(0, Math.min(parseInt(hex, 16), 255)))
        }
        var cr = hex2dec(val.substr(0, 2)),
            cg = hex2dec(val.substr(2, 2)),
            cb = hex2dec(val.substr(4, 2));
        return cr + ',' + cg + ',' + cb
    }
}

function getRGBColor(val, color) {
    if (!val.match(/^#[0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f]$/i)) {
        val = color || '#000000'
    }
    if (!isCV) {
        return val.substr(1, 6)
    } else {
        function hex2dec(hex) {
            return (Math.max(0, Math.min(parseInt(hex, 16), 255)))
        }
        var cr = hex2dec(val.substr(1, 2)),
            cg = hex2dec(val.substr(3, 2)),
            cb = hex2dec(val.substr(5, 2));
        return cr + ',' + cg + ',' + cb
    }
}

function getClassAttribute(classes, string) {
    var temp = 0,
        pos = string.length;
    for (var j = 0; j < classes.length; j++) {
        if (classes[j].indexOf(string) == 0) {
            temp = 1;
            break
        }
    }
    return temp
}

function fadeCanvas(id, opac) {
    var obj = $(id);
    if (obj.fading == 1 && opac <= 100) {
        obj.style.opacity = opac / 100;
        opac += 10;
        window.setTimeout("fadeCanvas('" + id + "'," + opac + ")", 10)
    }
}

function setAreaOver(obj, id, bd, co, op, nb, f, z) {
    var a, i, j, d, c, o, b, n, l, r, v, u, x, y, p, context, k = 0,
        t = '',
        r = obj.getAttribute('rel'),
        canvas = $(id);
    if (r != null) {
        d = r.split(",");
        v = d.unshift(obj.id)
    } else {
        d = new Array(obj.id)
    }

    function setAttr() {
        if (l.indexOf('forcegroup') != -1) {
            k = getClassAttribute(u, "forcegroup")
        } else {
            k = 0
        }
        if (l.indexOf('iopacity') != -1) {
            o = getClassValue(u, "iopacity") / 100
        } else {
            o = op
        }
        if (l.indexOf('iborder') != -1) {
            b = getClassRGBColor(u, "iborder", bd)
        } else {
            b = bd
        }
        if (l.indexOf('icolor') != -1) {
            c = getClassRGBColor(u, "rgb(133,73,195)", co)
        } else {
            c = co
        }
        if (l.indexOf('noborder') != -1) {
            n = getClassAttribute(u, "noborder")
        } else {
            n = nb
        }
    }
    if (isVM) { // for IE
        for (a = 0; a < d.length; a++) {
            obj = $(d[a]);
            p = '', l = obj.className, u = l.split(" "), v = obj.coords.split(",");
            if (k == 0) {
                setAttr()
            }
            if (canvas.innerHTML) {
                t = canvas.innerHTML;
            }
            if (obj.shape.toLowerCase() == 'rect') {
                t += '<v:rect unselectable="on" strokeweight="1" filled="t" stroked="' + (n < 1 ? "t" : "f") + '" strokecolor="#' + b + '" style="zoom:1;margin:0;padding:0;display:block;position:absolute;left:' + parseInt(v[0]) + 'px;top:' + parseInt(v[1]) + 'px;width:' + parseInt(v[2] - v[0]) + 'px;height:' + parseInt(v[3] - v[1]) + 'px;"><v:fill color="#' + c + '" opacity="' + o + '" /></v:rect>'
            } else if (obj.shape.toLowerCase() == 'circle') {
                t += '<v:oval unselectable="on" strokeweight="1" filled="t" stroked="' + (n < 1 ? "t" : "f") + '" strokecolor="#' + b + '" style="zoom:1;margin:0;padding:0;display:block;position:absolute;left:' + parseInt(v[0] - v[2]) + 'px;top:' + parseInt(v[1] - v[2]) + 'px;width:' + (parseInt(v[2]) * 2) + 'px;height:' + (parseInt(v[2]) * 2) + 'px;"><v:fill color="#' + c + '" opacity="' + o + '" /></v:oval>'
            } else {
                for (j = 2; j < v.length; j += 2) {
                    p += parseInt(v[j]) + ',' + parseInt(v[j + 1]) + ','
                }
                t += '<v:shape unselectable="on" strokeweight="1" filled="t" stroked="' + (n < 1 ? "t" : "f") + '" strokecolor="#' + b + '" coordorigin="0,0" coordsize="' + canvas.width + ',' + canvas.height + '" path="m ' + parseInt(v[0]) + ',' + parseInt(v[1]) + ' l ' + p + ' x e" style="zoom:1;margin:0;padding:0;display:block;position:absolute;top:0px;left:0px;width:' + canvas.width + 'px;height:' + canvas.height + 'px;"><v:fill color="#' + c + '" opacity="' + o + '" /></v:shape>'
            }
        }
        canvas.innerHTML = t
    } else if (isCV) {
        if (f < 1) {
            canvas.fading = 0;
            canvas.style.opacity = 0
        }
        context = canvas.getContext("2d");
        for (a = 0; a < d.length; a++) {
            obj = $(d[a]);
            l = obj.className, u = l.split(" "), v = obj.coords.split(",");
            if (k == 0) {
                setAttr()
            }
            context.beginPath();
            if (obj.shape.toLowerCase() == 'rect') {
                context.rect(0.5 + parseInt(v[0]), 0.5 + parseInt(v[1]), parseInt(v[2] - v[0]), parseInt(v[3] - v[1]));
                context.closePath()
            } else if (obj.shape.toLowerCase() == 'circle') {
                context.arc(0.5 + parseInt(v[0]), 0.5 + parseInt(v[1]), parseInt(v[2]), 0, (Math.PI / 180) * 360, false)
            } else {
                context.moveTo(parseInt(v[0]), parseInt(v[1]));
                for (j = 2; j < v.length; j += 2) {
                    context.lineTo(parseInt(v[j]), parseInt(v[j + 1]))
                }
                context.closePath()
            }
            context.fillStyle = 'rgba(' + c + ',' + o + ')';
            context.strokeStyle = 'rgba(' + b + ',1)';
            context.fill();
            if (n < 1) {
                context.stroke()
            }
        }
        if (f < 1) {
            canvas.fading = 1;
            fadeCanvas(id, 0)
        }
    } else {
        o = op;
        l = obj.className;
        u = l.split(" ");
        if (l.indexOf('forcegroup') != -1) {
            k = getClassAttribute(u, "forcegroup");
            if (k != 0) {
                if (l.indexOf('iopacity') != -1) {
                    o = getClassValue(u, "iopacity") / 100;
                    k = 0
                }
            }
        }
        if (isIE) {
            canvas.style.filter = "Alpha(opacity=" + (o * 100) + ")"
        } else {
            canvas.style.opacity = o;
            canvas.style.MozOpacity = o;
            canvas.style.KhtmlOpacity = o
        }
        for (a = 0; a < d.length; a++) {
            obj = $(d[a]);
            l = obj.className, u = l.split(" "), v = obj.coords.split(",");
            if (k == 0) {
                if (l.indexOf('forcegroup') != -1) {
                    k = getClassAttribute(u, "forcegroup")
                } else {
                    k = 0
                }
                if (l.indexOf('icolor') != -1) {
                    c = getClassRGBColor(u, "icolor", co)
                } else {
                    c = co
                }
            }
            jg[z].setColor("#" + c);
            if (obj.shape.toLowerCase() == 'rect') {
                jg[z].fillRect(parseInt(v[0]), parseInt(v[1]), parseInt(v[2] - v[0]) + 1, parseInt(v[3] - v[1]) + 1)
            } else if (obj.shape.toLowerCase() == 'circle') {
                jg[z].fillEllipse(parseInt(v[0] - v[2]), parseInt(v[1] - v[2]), parseInt(v[2]) * 2 + 1, parseInt(v[2]) * 2 + 1)
            } else {
                x = new Array();
                y = new Array();
                i = 0;
                for (j = 0; j < v.length; j += 2) {
                    x[i] = parseInt(v[j]);
                    y[i] = parseInt(v[j + 1]);
                    i++
                }
                jg[z].fillPolygon(x, y)
            }
            jg[z].paint()
        }
    }
}

function setAreaOut(id, z) {
    setAreaOut(false, id, true, z);
}

function setAreaOut(obj, id, z) {
    setAreaOut(obj, id, true, z);
}

/** Deletes ALL drawn araes */
function setAreaOut(obj, id, f, z) {
    var canvas = $(id);
    if (isVM) {
        canvas.innerHTML = ''
    } else if (isJG) {
        jg[z].clear()
    } else if (isCV) {
        var context = canvas.getContext("2d");
        context.clearRect(0, 0, canvas.width, canvas.height)
    }
}

function extAreaOver(image, area) {
    extAreaOver(image, area, false, false);
}

function extAreaOver(image, area, bgcolor) {
    extAreaOver(image, area, bgcolor);
}

function extAreaOver(image, area, bgcolor, opacity) {
    var img = $(image),
        obj = $(area),
        cav = $(img.canvasid),
        bc = getRGBColor('#000000', '#000000'),
        ac, op;
    if (!bgcolor) {
        ac = getRGBColor(img.options['areacolor'], '#0000ff');
    } else {
        ac = getRGBColor(bgcolor, '#0000ff');
    }
    if (!opacity) {
        op = img.options['opacity'] / 100;
    } else {
        op = parseInt(opacity) / 100;
    }
    if (img && obj && cav) setAreaOver(obj, img.canvasid, bc, ac, op, img.options['noborder'], img.options['nofade'], img.jg)
}

function extAreaOut(image) {
    extAreaOut(image, '');
}

function extAreaOut(image, area) {
    var img = $(image),
        obj = false;
    if (area) {
        obj = $(area);
    }
    if (img) setAreaOut(obj, img.canvasid, true, img.jg);
}

function getCoords(e, n, a, i, x, y, w, h, pw, ph) {
    var t, o, ox, oy, ex, ey, cx, cy, px = 0,
        py = 0;
    if (!e) {
        e = window.event
    }
    if (e.pageX || e.pageY) {
        px = e.pageX;
        py = e.pageY
    }
    ex = e.clientX;
    ey = e.clientY;
    if (self.pageXOffset || self.pageYOffset) {
        ox = self.pageXOffset;
        if (ox > 0 && px == ex) {
            ex -= ox
        }
        oy = self.pageYOffset;
        if (oy > 0 && py == ey) {
            ey -= oy
        }
    } else if (document.documentElement) {
        ox = document.documentElement.scrollLeft;
        oy = document.documentElement.scrollTop
    } else if (document.body) {
        ox = document.body.scrollLeft;
        oy = document.body.scrollTop
    }
    if (document.body.scrollHeight != ph || document.body.scrollWidth != pw) {
        var o = $(i),
            t = findPosXY(o);
        x = t.x;
        y = t.y
    }
    cx = Math.min(Math.max(ex + ox - x, 0), w);
    cy = Math.min(Math.max(ey + oy - y, 0), h);
    // showCoords(n, a, cx, cy, w, h)
}

function findPosXY(ele) {
    var t,
        d = {
            x: ele.offsetLeft,
            y: ele.offsetTop
        };
    if (ele.offsetParent) {
        t = findPosXY(ele.offsetParent);
        d.x += t.x;
        d.y += t.y
    }
    return d
}

function roundedRect(ctx, x, y, width, height, radius, nopath) {
    if (!nopath) ctx.beginPath();
    ctx.moveTo(x, y + radius);
    ctx.lineTo(x, y + height - radius);
    ctx.quadraticCurveTo(x, y + height, x + radius, y + height);
    ctx.lineTo(x + width - radius, y + height);
    ctx.quadraticCurveTo(x + width, y + height, x + width, y + height - radius);
    ctx.lineTo(x + width, y + radius);
    ctx.quadraticCurveTo(x + width, y, x + width - radius, y);
    ctx.lineTo(x + radius, y);
    ctx.quadraticCurveTo(x, y, x, y + radius);
    if (!nopath) ctx.closePath()
}

function getRadius(radius, width, height) {
    var part = (Math.min(width, height) / 100);
    radius = Math.max(Math.min(100, radius / part), 0);
    return radius + '%'
}

var cvi_map = {
    defaultRadius: 0,
    defaultOpacity: 25,
    defaultBordercolor: '#0000ff',
    defaultAreacolor: '#000000',
    defaultNoborder: false,
    defaultNofade: false,
    defaultShowcoords: false,
    defaultImgsrc: '',
    defaultMapid: '',
    defaultDelayed: false,
    add: function (image, options) {
        var map, mapname = image.useMap.split("#");
        if (mapname[1] != '' && mapname[1].length >= 1) {
            map = document.getElementsByName(mapname[1])[0]
        }
        if (image.tagName.toUpperCase() == "IMG" && map) {
            var defopts = {
                "radius": cvi_map.defaultRadius,
                "opacity": cvi_map.defaultOpacity,
                "bordercolor": cvi_map.defaultBordercolor,
                "areacolor": cvi_map.defaultAreacolor,
                "noborder": cvi_map.defaultNoborder,
                "nofade": cvi_map.defaultNofade,
                "showcoords": cvi_map.defaultShowcoords,
                "imgsrc": cvi_map.defaultImgsrc,
                "mapid": cvi_map.defaultMapid,
                "delayed": cvi_map.defaultDelayed
            };
            if (options) {
                for (var i in defopts) {
                    if (!options[i]) {
                        options[i] = defopts[i]
                    }
                }
            } else {
                options = defopts
            }
            var imageWidth = ('iwidth' in options) ? parseInt(options.iwidth) : image.width,
                imageHeight = ('iheight' in options) ? parseInt(options.iheight) : image.height;
            if (imageWidth > 0 && imageHeight > 0) {
                try {
                    var object = image.parentNode,
                        blind, 
                        bgrnd, 
                        canvas;
                    object.style.position = (object.style.position == 'static' || object.style.position == '' ? 'relative' : object.style.position);
                    object.style.height = imageHeight + 'px';
                    object.style.width = imageWidth + 'px';
                    object.style.padding = 0 + 'px';
                    object.style.MozUserSelect = "none";
                    object.style.KhtmlUserSelect = "none";
                    object.unselectable = "on";
                    image.jg = 0;
                    if (isVM) {
                        if (document.namespaces['v'] == null) {
                            var e = ["shape", "shapetype", "group", "background", "path", "formulas", "handles", "fill", "stroke", "shadow", "textbox", "textpath", "imagedata", "line", "polyline", "curve", "roundrect", "oval", "rect", "arc", "image"],
                                s = document.createStyleSheet();
                            for (var i = 0; i < e.length; i++) {
                                s.addRule("v\\:" + e[i], "behavior: url(#default#VML);")
                            }
                            document.namespaces.add("v", "urn:schemas-microsoft-com:vml")
                        }
                        canvas = document.createElement(['<var style="zoom:1;overflow:hidden;display:block;width:' + imageWidth + 'px;height:' + imageHeight + 'px;padding:0;">'].join(''));
                        bgrnd = document.createElement(['<var style="zoom:1;overflow:hidden;display:block;width:' + imageWidth + 'px;height:' + imageHeight + 'px;padding:0;">'].join(''));
                        dummy = document.createElement(['<var style="zoom:1;overflow:hidden;display:block;width:' + imageWidth + 'px;height:' + imageHeight + 'px;padding:0;">'].join(''))
                    } else if (isCV) {
                        canvas = document.createElement('canvas');
                        bgrnd = document.createElement('canvas')
                    } else {
                        canvas = document.createElement('div');
                        bgrnd = document.createElement('img');
                        bgrnd.src = image.src;
                        if (typeof (window['jsGraphics']) !== 'undefined') {
                            image.jg = parseInt(jg.length);
                            jg[image.jg] = new jsGraphics(canvas);
                            isJG = 1
                        }
                    }
                    canvas.id = image.id + '_canvas';
                    canvas.style.height = imageHeight + 'px';
                    canvas.style.width = imageWidth + 'px';
                    canvas.height = imageHeight;
                    canvas.width = imageWidth;
                    canvas.left = 0;
                    canvas.top = 0;
                    canvas.style.position = 'absolute';
                    canvas.style.left = 0 + 'px';
                    canvas.style.top = 0 + 'px';
                    canvas.fading = 0;
                    image.className = '';
                    image.style.cssText = '';
                    image.left = 0;
                    image.top = 0;
                    image.style.position = 'absolute';
                    image.style.height = imageHeight + 'px';
                    image.style.width = imageWidth + 'px';
                    image.style.left = 0 + 'px';
                    image.style.top = 0 + 'px';
                    image.style.MozUserSelect = "none";
                    image.style.KhtmlUserSelect = "none";
                    image.unselectable = "on";
                    image.options = options;
                    image.canvasid = image.id + '_canvas';
                    image.dummyid = image.id + '_dummy';
                    image.bgrndid = image.id + '_image';
                    image.blindid = map.name + '_blind';
                    image.mapname = map.name;
                    image.areas = map.innerHTML;
                    image.active = false;
                    if (isIE) {
                        image.style.filter = "Alpha(opacity=0)"
                    } else {
                        image.style.opacity = 0;
                        image.style.MozOpacity = 0;
                        image.style.KhtmlOpacity = 0
                    }
                    bgrnd.id = image.bgrndid;
                    bgrnd.left = 0;
                    bgrnd.top = 0;
                    bgrnd.style.position = 'absolute';
                    bgrnd.style.height = imageHeight + 'px';
                    bgrnd.style.width = imageWidth + 'px';
                    bgrnd.style.left = 0 + 'px';
                    bgrnd.style.top = 0 + 'px';
                    bgrnd.height = imageHeight;
                    bgrnd.width = imageWidth;
                    blind = document.createElement('div');
                    blind.id = image.blindid;
                    blind.className = "blind_area";
                    blind.left = 0;
                    blind.top = 0;
                    blind.style.position = 'absolute';
                    blind.style.height = imageHeight + 'px';
                    blind.style.width = imageWidth + 'px';
                    blind.style.left = 0 + 'px';
                    blind.style.top = 0 + 'px';
                    blind.innerHTML = " ";
                    object.insertBefore(canvas, image);
                    object.insertBefore(bgrnd, canvas);
                    object.insertBefore(blind, image);
                    cvi_map.modify(image, options)
                } catch (e) {}
            }
        }
    },
    modify: function (image, options) {
        try {
            // var radius = (typeof options['radius'] == 'number' ? options['radius'] : image.options['radius']);
            // image.options['radius'] = radius;
            var opacity = (typeof options['opacity'] == 'number' ? options['opacity'] : image.options['opacity']),
                bcolor = (typeof options['bordercolor'] == 'string' ? options['bordercolor'] : image.options['bordercolor']),
                acolor = (typeof options['areacolor'] == 'string' ? options['areacolor'] : image.options['areacolor']),
                noborder = (typeof options['noborder'] == 'boolean' ? options['noborder'] : image.options['noborder']),
                nofade = (typeof options['nofade'] == 'boolean' ? options['nofade'] : image.options['nofade']),
                showcoords = (typeof options['showcoords'] == 'boolean' ? options['showcoords'] : image.options['showcoords']),
                delayed = (typeof options['delayed'] == 'boolean' ? options['delayed'] : image.options['delayed']),
                imgsrc = (typeof options['imgsrc'] == 'string' ? options['imgsrc'] : image.options['imgsrc']),
                mapid = (typeof options['mapid'] == 'string' ? options['mapid'] : image.options['mapid']),
                context, 
                func, 
                ih = image.height,
                iw = image.width,
                // radius = parseInt(Math.min(Math.min(iw / 4, ih / 4), radius)),
                radius = 0,
                opacity = opacity == 0 ? 0.0 : opacity / 100,
                bcolor = getRGBColor(bcolor, '#0000ff'),
                acolor = getRGBColor(acolor, '#000000'),
                canvas = $(image.canvasid),
                dummy = $(image.dummyid),
                bgrnd = $(image.bgrndid),
                map = document.getElementsByName(image.mapname)[0],
                opac = 100;

            image.options['radius'] = 0;
            image.options['opacity'] = opacity;
            image.options['bordercolor'] = bcolor;
            image.options['areacolor'] = acolor;
            image.options['noborder'] = noborder;
            image.options['nofade'] = nofade;
            image.options['showcoords'] = showcoords;
            image.options['delayed'] = delayed;
            image.options['imgsrc'] = imgsrc;
            image.options['mapid'] = mapid;
            
            function replace() {
                if (mapid != '') {
                    var tmp = $(mapid);
                    if (tmp) {
                        map.innerHTML = tmp.innerHTML
                    }
                } else {
                    map.innerHTML = image.areas
                }
            }

            function prepare() {
                if (!delayed) {
                    replace()
                }
                for (var j = 0; j < map.areas.length; j++) {
                    if (map.areas[j].shape.match(/(rect|poly|circle)/i)) {
                        if (window.opera || map.areas[j].coords != '') {
                            if (map.areas[j].id == '') {
                                map.areas[j].id = image.mapname + '_' + j
                            }
                        }
                    }
                }
                if (showcoords) {
                    var atr, t = findPosXY(image),
                        ph = document.body.scrollHeight,
                        pw = document.body.scrollWidth;
                    if (isVM || isIE) {
                        image.onmousemove = new Function('getCoords(event,"' + image.mapname + '",0,"' + image.id + '",' + t.x + ',' + t.y + ',' + iw + ',' + ih + ',' + pw + ',' + ph + ');' + image.movefunc)
                    } else {
                        image.setAttribute("onmousemove", "getCoords(event,'" + image.mapname + "',0,'" + image.id + "'," + t.x + "," + t.y + "," + iw + "," + ih + "," + pw + "," + ph + ");" + image.movefunc)
                    }
                    if (map.length > 0) {
                        for (var j = 0; j < map.areas.length; j++) {
                            if (map.areas[j].shape.match(/(rect|poly|circle)/i)) {
                                if (window.opera || map.areas[j].coords != '') {
                                    atr = map.areas[j].id;
                                    if (isVM || isIE) {
                                        func = map.areas[j].onmousemove;
                                        if (func != null) {
                                            tmp = String(func);
                                            func = (tmp.indexOf('function') >= 0 ? tmp.match(/\{([^}]+)\}/) : tmp);
                                            func = (typeof (func) == 'object' ? func[1] : func)
                                        }
                                        map.areas[j].onmousemove = new Function('getCoords(event,"' + image.mapname + '","' + atr + '","' + image.id + '",' + t.x + ',' + t.y + ',' + iw + ',' + ih + ',' + pw + ',' + ph + ');' + func)
                                    } else {
                                        func = map.areas[j].getAttribute("onmousemove");
                                        map.areas[j].setAttribute("onmousemove", "getCoords(event,'" + image.mapname + "','" + atr + "','" + image.id + "'," + t.x + "," + t.y + "," + iw + "," + ih + "," + pw + "," + ph + ");" + func)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (canvas && bgrnd && map) {
                if (isCV) {
                    context = canvas.getContext("2d");
                    context.clearRect(0, 0, canvas.width, canvas.height)
                } else if (isVM) {
                    canvas.innerHTML = ''
                } else {
                    if (isIE) {
                        canvas.style.filter = "Alpha(opacity=" + (opacity * 100) + ")"
                    } else {
                        canvas.style.opacity = opacity;
                        canvas.style.MozOpacity = opacity;
                        canvas.style.KhtmlOpacity = opacity
                    }
                    if (isJG) {
                        jg[image.jg].clear()
                    }
                }
                if (delayed) {
                    replace()
                }
                if (isCV) {
                    if (imgsrc != '') {
                        var img = new Image();
                        img.onload = function () {
                            context = bgrnd.getContext("2d");
                            context.clearRect(0, 0, bgrnd.width, bgrnd.height);
                            context.save();
                            // if (radius > 0) {
                                // roundedRect(context, 0, 0, bgrnd.width, bgrnd.height, radius);
                                // context.clip()
                            // }
                            context.fillStyle = 'rgba(0,0,0,0)';
                            context.fillRect(0, 0, bgrnd.width, bgrnd.height);
                            context.drawImage(img, 0, 0, bgrnd.width, bgrnd.height);
                            context.restore();
                            prepare()
                        };
                        img.src = imgsrc
                    } else {
                        context = bgrnd.getContext("2d");
                        context.clearRect(0, 0, bgrnd.width, bgrnd.height);
                        context.save();
                        // if (radius > 0) {
                            // roundedRect(context, 0, 0, bgrnd.width, bgrnd.height, radius);
                            // context.clip()
                        // }
                        context.fillStyle = 'rgba(0,0,0,0)';
                        context.fillRect(0, 0, bgrnd.width, bgrnd.height);
                        context.drawImage(image, 0, 0, bgrnd.width, bgrnd.height);
                        context.restore();
                        prepare()
                    }
                } else if (isVM) {
                    // if (radius > 0) {
                        // radius = getRadius(radius, bgrnd.width, bgrnd.height)
                    // }
                    bgrnd.innerHTML = '<v:roundrect arcsize="' + radius + '" strokeweight="0" filled="t" stroked="f" fillcolor="#ffffff" style="zoom:1;margin:0;padding:0;display:block;position:absolute;left:0px;top:0px;width:' + bgrnd.width + 'px;height:' + bgrnd.height + 'px;"><v:fill src="' + (imgsrc != '' ? imgsrc : image.src) + '" type="frame" /></v:roundrect>';
                    prepare()
                } else {
                    bgrnd.src = imgsrc != '' ? imgsrc : image.src;
                    prepare()
                }
                image.active = true;
                if (isVM || isIE) {
                    func = image.onmousemove;
                    if (func != null) {
                        tmp = String(func);
                        func = (tmp.indexOf('function') >= 0 ? tmp.match(/\{([^}]+)\}/) : tmp);
                        func = (typeof (func) == 'object' ? func[1] : func)
                    }
                    image.movefunc = func
                } else {
                    image.movefunc = image.getAttribute("onmousemove")
                }
            }
        } catch (e) {}
    }
}

cvi_map.defaultOpacity = 25;
cvi_map.defaultBordercolor = '#BBBBFF';
cvi_map.defaultAreacolor = '#BBBBFF';
cvi_map.defaultNoborder = true;
cvi_map.defaultNofade = true;
cvi_map.defaultShowcoords = false;
cvi_map.defaultDelayed = false;