---
name: MediMeal Health & Nourishment
colors:
  surface: '#fbf9f8'
  surface-dim: '#dbdad9'
  surface-bright: '#fbf9f8'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f5f3f2'
  surface-container: '#efedec'
  surface-container-high: '#e9e8e7'
  surface-container-highest: '#e4e2e1'
  on-surface: '#1b1c1b'
  on-surface-variant: '#554243'
  inverse-surface: '#303030'
  inverse-on-surface: '#f2f0ef'
  outline: '#877273'
  outline-variant: '#dac0c1'
  surface-tint: '#9a414e'
  primary: '#37000c'
  on-primary: '#ffffff'
  primary-container: '#570d1d'
  on-primary-container: '#da737f'
  inverse-primary: '#ffb2b9'
  secondary: '#a23a4b'
  on-secondary: '#ffffff'
  secondary-container: '#ff8292'
  on-secondary-container: '#76182c'
  tertiary: '#370009'
  on-tertiary: '#ffffff'
  tertiary-container: '#5d0016'
  on-tertiary-container: '#fe566b'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffdadc'
  primary-fixed-dim: '#ffb2b9'
  on-primary-fixed: '#40000f'
  on-primary-fixed-variant: '#7c2a37'
  secondary-fixed: '#ffdadc'
  secondary-fixed-dim: '#ffb2b9'
  on-secondary-fixed: '#400010'
  on-secondary-fixed-variant: '#832235'
  tertiary-fixed: '#ffdada'
  tertiary-fixed-dim: '#ffb3b6'
  on-tertiary-fixed: '#40000c'
  on-tertiary-fixed-variant: '#920028'
  background: '#fbf9f8'
  on-background: '#1b1c1b'
  surface-variant: '#e4e2e1'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 40px
    fontWeight: '800'
    lineHeight: 48px
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '800'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
    letterSpacing: -0.015em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 22px
    fontWeight: '700'
    lineHeight: 28px
    letterSpacing: -0.01em
  title-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
    letterSpacing: -0.005em
  title-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 22px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 18px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.02em
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 11px
    fontWeight: '700'
    lineHeight: 14px
    letterSpacing: 0.04em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  margin: 1.25rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

The design system embodies calm vigilance, clinical reliability, and epicurean warmth. It bridges medical schedule compliance with intentional meal curation. Designed specifically for Android's modern hardware and ergonomic hand-reach zones, the design language rejects clinical coldness in favor of a luxurious, reassuring atmosphere. 

The aesthetic is **Tactile Warm Modern**, blending fluid Material 3 dynamic surfaces with bespoke editorial styling. Rich burgundy and wine tones evoke vitality, blood health, and artisanal nutrition, while soft warm-white neutrals reduce eye fatigue during dawn doses and midnight health checks. Micro-surfaces mimic smooth ceramic and pill-coated textures with subtle tactile press depths and squircle geometries.

## Colors

The palette establishes an authoritative yet nurturing health interface. The primary spectrum flows through deep organic wines to vibrant vital crimsons, contrasted by organic cream canvases and balanced clinical feedback tones.

### Light Palette Role Distribution
- **Primary (`#570D1D`)**: Core brand anchor, primary action surfaces, navigation indicators, high-contrast headings.
- **Secondary (`#75172B`)**: Nested card banners, category headers, progressive timeline rings.
- **Tertiary (`#D63850`)**: Critical medication alarms, active toggle highlights, urgent scheduling badges.
- **Sub-Accent Rich Crimson (`#A51F36`)**: Mid-tier interactive elements, secondary action outlines, interactive graph peaks.
- **Tonal Container Soft Rose (`#F6E8EB`)**: Medication chip fills, completed dosage backgrounds, subtle hover/focus fields.
- **Surface & Background**:
  - App Canvas: `#F8F6F5`
  - Elevated Container / Warm White: `#FFFDFC`
  - Border & Hairline Divider: `#EADBDD`
- **Text & Content**:
  - Primary Text: `#21191B`
  - Secondary / Muted Text: `#817579`
- **Functional Semantics**:
  - Success / Meal-Taken State: `#28845C` (paired with container `#E4F4EC`)
  - Warning / Missed Dose Window: `#C97712` (paired with container `#FDF1E2`)
  - Critical / Contraindication: `#D63850` (paired with container `#FCE8EB`)

### Dark Palette Specifications
- **Dark Background**: `#151113`
- **Dark Surface Container**: `#21191C`
- **Dark Elevated Container**: `#2D2024`
- **Dark Text Primary**: `#FFF7F8`
- **Dark Text Muted**: `#B39DA4`
- **Dark Accent Primary**: `#E34A62`
- **Dark Success**: `#3EAA7B`

## Typography

Typography relies on **Plus Jakarta Sans** across all roles, selected for its open letter apertures, precise geometric structure, and organic terminal curves that render crisply on high-density OLED mobile displays.

- **Numerics & Timing**: Dosage schedules and nutrition counters employ `title-lg` or `display-lg-mobile` with tabular figure settings (`tnum`) to keep intake counts visually aligned in data rows.
- **Legibility in Motion**: Medication intake instructions (e.g., "Take after breakfast with full glass of water") default to `body-md` with 20px line heights, ensuring swift, unambiguous comprehension during groggy morning routines.
- **Labels & Micro-data**: All `label-sm` elements represent schedule metadata (e.g., "FASTING REQUIRED") set in deliberate tracking to avoid visual crowding.

## Layout & Spacing

Layout adheres strictly to an **8dp primary grid** with a **4dp sub-grid** tailored to Android touch interactions and one-handed reach zones.

- **Vertical Cadence**: Screen layouts reserve top app areas for macro status dashboards, while primary interactive components (intake triggers, bottom navigation) live within the 45% thumb sweep area.
- **Screen Margins**: Screen canvas margins sit at `margin` (20dp / 1.25rem), providing sufficient border breathing space around high-density data cards.
- **Card Padding**: Micro-cards and schedule items employ `space-md` (16dp) internal padding; feature hero cards and intake cards expand to `space-lg` (24dp).
- **Responsive Adaptations**:
  - *Compact (Phone < 600dp)*: 4-column fluid layout with single-stream vertical timeline progression.
  - *Medium (Foldables / Tablets 600dp - 840dp)*: 8-column layout splitting the timeline to the left (5 cols) and nutrition log/calendar details to the right (3 cols).

## Elevation & Depth

Visual depth is communicated through **Tonal Layers paired with Tinted Wine Ambient Shadows** rather than stark grey drops. This establishes a soft, physical feel akin to stacked medical journals and organic tablets.

- **Level 0 (Flat Canvas)**: Hex `#F8F6F5` (`#151113` in Dark Mode). Un-elevated resting field.
- **Level 1 (Card & Section Containers)**: Warm White `#FFFDFC` (`#21191C` in Dark Mode) with an ambient shadow: `0px 4px 16px -2px rgba(87, 13, 29, 0.04), 0px 1px 3px 0px rgba(87, 13, 29, 0.02)`. Border stroke: 1dp solid `#F1E3E6`.
- **Level 2 (Interactive Floating Elements, Timeline Nodes)**: `#FFFDFC` elevated with `0px 8px 24px -4px rgba(87, 13, 29, 0.08), 0px 2px 6px -1px rgba(87, 13, 29, 0.04)`.
- **Level 3 (Floating Bar & Bottom Sheets)**: Backdrop blur combined with semi-opaque wine-tinted scrim: `0px 16px 36px -6px rgba(87, 13, 29, 0.16)`.
- **Level Hero (Primary Gradient Surfaces)**: Linear gradient 135deg from `#75172B` to `#570D1D`, cast with a glowing specular shadow: `0px 12px 32px -4px rgba(117, 23, 43, 0.35)`.

## Shapes

The system relies on smooth squircles (`roundedness: 2`) that mimic pharmaceutical pill caps, clinical lozenges, and ceramic dishware:

- **Cards & Schedule Panels**: Built with `rounded-xl` (24dp) for calm, non-aggressive card enclosures.
- **Input Fields & Modal Dialogs**: Standardized at `rounded-lg` (16dp).
- **Buttons, Medication Badges, and Pill Trackers**: Rendered in full pill-shape (`rounded-full` / 9999dp) ensuring strong visual correspondence to real-world tablets and nutritional capsules.

## Components

### Buttons
- **Primary CTA**: Full-pill shape (52dp height). Gradient from `#75172B` to `#570D1D` with `#FFFDFC` text (`label-lg`). Deep pressed state compresses down via scale transform (`0.97`) with immediate haptic trigger.
- **Secondary / Meal Link Button**: Soft Rose `#F6E8EB` fill with `#570D1D` text, 0dp border. On press: `#EDD4D9`.
- **Tertiary / Quick Action**: Ghost button with 1.5dp `#570D1D` border and transparent background.

### Timeline Trackers & Racetrack Nodes
- A continuous 2dp vertical conduit rendered in `#EADBDD` connecting circular and pill-shaped racetrack nodes.
- **Active Node**: 36dp pill container filled with `#D63850` containing a pulse indicator ring.
- **Completed Node**: 28dp circular container in `#28845C` with white checkmark icon.
- **Future Node**: 24dp circular outline in `#817579` with a subtle `#F8F6F5` core.

### Medication Tracker Pill Badges
- 32dp height pill component showing dosage metadata (e.g., "500mg • With Meal").
- Composed of a segmented pill icon in `#570D1D`, text in `label-md`, set against `#F6E8EB`. If taken, shifts dynamically to `#E4F4EC` with `#28845C` accent text.

### Cards
- **Meal & Med Schedule Card**: 20dp squircle corner radius, `#FFFDFC` background, 1dp subtle border in `#F1E3E6`. Contains distinct left-edge chromatic pill accent indicating requirement (e.g., green for post-meal, amber for empty-stomach).
- **Hero Insight Card**: Rich Burgundy gradient (`#75172B` to `#570D1D`) with subtle specular inner glow (`inset 0px 1px 1px rgba(255, 255, 255, 0.15)`), white display text, and high-contrast `#FFFDFC` badge elements.

### Inputs & Selectors
- **Meal/Dose Form Inputs**: 56dp height with `rounded-lg` (16dp) corners. Resting background `#FFFDFC` with 1dp border `#EADBDD`. Focused state transitions border to 2dp `#75172B` with zero shadow disruption.
- **Checkboxes & Dosage Radios**: 24dp rounded squares and circular selectors using `#570D1D` fill with smooth spring animation check glyphs.

### Floating Navigation Bar
- Android pill bar floating 16dp above the home indicator. 
- Translucent tinted white glass (`rgba(255, 253, 252, 0.92)` with `16px` backdrop blur) in Light Mode, and (`rgba(33, 25, 28, 0.90)` with `16px` backdrop blur) in Dark Mode.
- Active tab pill indicator expands smoothly in `#F6E8EB` with `#570D1D` icon tinting.