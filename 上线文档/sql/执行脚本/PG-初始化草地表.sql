CREATE SEQUENCE IF NOT EXISTS global_caodi_gid_seq
            INCREMENT 1
            START 1
            MINVALUE 1
            MAXVALUE 2147483647
            CACHE 1;

CREATE TABLE "public"."global_caodi" (
  "gid" int4 NOT NULL DEFAULT nextval('global_caodi_gid_seq'::regclass),
  "geom" "public"."geometry",
  "minx" float8,
  "miny" float8,
  "maxx" float8,
  "maxy" float8,
  "layertype" varchar(50) COLLATE "pg_catalog"."default",
  "calarea" float8,
  "properties" text COLLATE "pg_catalog"."default",
  "bsm" varchar(50) COLLATE "pg_catalog"."default",
  "dl" int4,
  "cd" float8,
  "areacode" varchar(500) COLLATE "pg_catalog"."default",
  "mc_1h" float8,
  "latitude" float8,
  "canheight" float8,
  "mc_herb" float8,
  "mc_100h" float8,
  "longitude" float8,
  "cancover" float8,
  "mc_10h" float8,
  "mc_woody" float8,
  "tvdi" float8,
  "crownratio" float8,
  CONSTRAINT "global_caodi_pkey" PRIMARY KEY ("gid")
)
;

CREATE INDEX "global_caodi_bsm_idx" ON "public"."global_caodi" USING btree (
  "bsm" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

CREATE INDEX "global_caodi_geom_idx" ON "public"."global_caodi" USING gist (
  "geom" "public"."gist_geometry_ops_2d"
);